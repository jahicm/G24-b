package ch.g24.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Service
public class AnalysisService {

    @Value("${analisys-ai-url}")
    private String analisys_url;


    public ResponseEntity<String> forwardFileToAIServer(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        try {


            // Prepare multipart request for AI server
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Correct method

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            // Send to AI server (replace with actual AI server URL)
            RestTemplate restTemplate = new RestTemplate();
            String aiServerUrl = analisys_url; // Replace with AI server URL
            ResponseEntity<String> response = restTemplate.exchange(
                    aiServerUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    public String getDashboard(long userId)
    {
        String result = "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"latest_readings\": {\n" +
                "            \"current\": {\n" +
                "                \"value\": 118,\n" +
                "                \"unit\": \"mg/dL\",\n" +
                "                \"status\": \"High\",\n" +
                "                \"timestamp\": \"2024-04-25T08:15:00\",\n" +
                "                \"predictedHbA1c\": \"5.8\"\n" +
                "            },\n" +
                "            \"weekly_average\": {\n" +
                "                \"value\": 98,\n" +
                "                \"unit\": \"mg/dL\",\n" +
                "                \"status\": \"low\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"weekly_overview\": {\n" +
                "            \"readings\": [\n" +
                "                { \"date\": \"2025-07-11\", \"value\": 110, \"unit\": \"mg/dL\" },\n" +
                "                { \"date\": \"2025-07-12\", \"value\": 90, \"unit\": \"mg/dL\" },\n" +
                "                { \"date\": \"2025-07-14\", \"value\": 85, \"unit\": \"mg/dL\" },\n" +
                "                { \"date\": \"2025-07-15\", \"value\": 95, \"unit\": \"mg/dL\" }\n" +
                "            ],\n" +
                "            \"time_range\": {\n" +
                "                \"start\": \"2025-07-11\",\n" +
                "                \"end\": \"2025-07-17\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"medications\": [\n" +
                "            {\n" +
                "                \"name\": \"Glimepiride\",\n" +
                "                \"type\": \"Sulfonylurea\",\n" +
                "                \"dosage\": \"2mg\",\n" +
                "                \"frequency\": \"Once daily\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"name\": \"Metformin\",\n" +
                "                \"type\": \"Biguanide\",\n" +
                "                \"dosage\": \"850mg\",\n" +
                "                \"frequency\": \"Twice daily\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"ai_analysis\": {\n" +
                "            \"summary\": {\n" +
                "                \"weekly_avg\": 98,\n" +
                "                \"unit\": \"mg/dL\",\n" +
                "                \"trend\": \"Stable\"\n" +
                "            },\n" +
                "            \"high_readings\": {\n" +
                "                \"count\": 3,\n" +
                "                \"threshold\": 150\n" +
                "            },\n" +
                "            \"time_analysis\": {\n" +
                "                \"best\": { \"range\": \"06:00-09:00\", \"avg_value\": 85 },\n" +
                "                \"worst\": { \"range\": \"20:00-23:00\", \"avg_value\": 125 }\n" +
                "            },\n" +
                "            \"recommendations\": [\n" +
                "                \"Reduce carbohydrate intake after 8 PM\",\n" +
                "                \"Consider evening walk after dinner\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"smart_insight\": {\n" +
                "            \"text\": \"Deine abendlichen Blutzuckerwerte sind regelmäßig erhöht. Versuche, das Abendessen 1 Stunde früher zu nehmen.\",\n" +
                "            \"translation\": \"Your evening blood glucose levels are regularly elevated. Try having dinner 1 hour earlier.\",\n" +
                "            \"context\": \"evening_hyperglycemia\",\n" +
                "            \"priority\": \"high\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"metadata\": {\n" +
                "        \"generated_at\": \"2025-07-17T10:00:00Z\",\n" +
                "        \"api_version\": \"1.0\"\n" +
                "    }\n" +
                "}";

        return result;
    }
}
