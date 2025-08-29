package ch.g24.api.services;

import ch.g24.api.models.DeepSeekResult;
import ch.g24.api.models.Reading;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DeepSeekService {

    @Value("${DEEPSEEK_API_KEY}")
    private String DEEPSEEK_API_KEY;

    private final String DEEPSEEK_URL = "https://api.deepseek.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public DeepSeekResult getAiAnalysis(Map<String, Object> patientData) throws Exception {

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content",
                "You are a diabetes assistant. Based on glucoseData (last 90 days), return AI analysis and smart insights in strict JSON format. " +
                        "Return the following JSON structure only, no extra text, no markdown, no backticks:\n" +
                        "- ai_analysis.summary: object { weekly_avg: number, unit: string, trend: string }\n" +
                        "- ai_analysis.high_readings: object { count: number, threshold: number }\n" +
                        "- ai_analysis.time_analysis: object { best: {range: string, avg_value: number}, worst: {range: string, avg_value: number} }\n" +
                        "- ai_analysis.recommendations: array of strings\n" +
                        "- ai_analysis.hba1c_prediction: object { value: number, unit: string }\n" +
                        "- smart_insight: object { text: string, translation: string, context: string, priority: string }"
        ));

        messages.add(Map.of(
                "role", "user",
                "content", "Analyze this glucose data: " + objectMapper.writeValueAsString(patientData)
        ));

        // 2️⃣ Prepare payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("messages", messages);

        // 3️⃣ Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + DEEPSEEK_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // 4️⃣ Call DeepSeek API
        ResponseEntity<Map> response = restTemplate.postForEntity(DEEPSEEK_URL, request, Map.class);
        Map<String, Object> body = response.getBody();

        // 5️⃣ Extract JSON string safely
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String rawContent = (String) message.get("content");

        // 6️⃣ Clean the string: remove backticks and extra whitespace
        String jsonString = rawContent.replaceAll("^[`\\s]+|[`\\s]+$", "").trim();

        // 7️⃣ Map JSON string to DeepSeekResult DTO
        return objectMapper.readValue(jsonString, DeepSeekResult.class);
    }

}
