package ch.g24.api.services;

import ch.g24.api.enums.SugarUnit;
import ch.g24.api.models.*;
import ch.g24.api.repository.entities.DataEntity;
import ch.g24.api.repository.entities.MedicationEntity;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.MedicationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class AnalysisService {

    @Value("${DEEPSEEK_URL}")
    private String DEEPSEEK_URL;
    @Value("${DEEPSEEK_API_KEY}")
    private String DEEPSEEK_API_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DataRepository dataRepository;
    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;
    private final String prompt = "Please analyze this lab report and summarize the findings.";
    private final RestTemplate restTemplate = new RestTemplate();

    public AnalysisService(DataRepository dataRepository, MedicationRepository medicationRepository, UserRepository userRepository) {
        this.dataRepository = dataRepository;
        this.medicationRepository = medicationRepository;
        this.userRepository = userRepository;
    }

    public String forwardPdfToDeepSeek(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return "No file uploaded or file is empty";
        }

        // 1️⃣ Prepare messages
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", "You are a diabetes assistant. Based on file, return AI analysis in strict JSON format."
        ));
        messages.add(Map.of(
                "role", "user",
                "content", "Analyze this file: " + convertPDFToText(file)
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

        if (body == null || !body.containsKey("choices")) {
            throw new RuntimeException("Invalid response from DeepSeek API");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

        Object rawContent = message.get("content");
        Map<String, Object> analysis;

        if (rawContent instanceof String) {
            analysis = objectMapper.readValue((String) rawContent, Map.class);
        } else if (rawContent instanceof Map) {
            Map<String, Object> contentMap = (Map<String, Object>) rawContent;
            analysis = (Map<String, Object>) contentMap.get("analysis");
        } else {
            throw new RuntimeException("Unexpected content type: " + rawContent.getClass());
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analysis);
    }
    private String convertPDFToText(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            PDDocument document = Loader.loadPDF(bytes);
            if (document.isEncrypted()) {
                System.out.println("PDF is encrypted - cannot extract text");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public DashBoardData getDashboard(long userId) {
        List<DataEntity> listOfEntries = dataRepository.getDataByUserId(userId).stream().sorted(Comparator.comparing(DataEntity::getMeasurementEntryTime).reversed()).toList();
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found: " + userId));

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        DashBoardData dashBoardData = new DashBoardData();
        double weeklyAverageDouble;

        if (listOfEntries.isEmpty())
            throw new RuntimeException("No Data found");

        List<Reading> listOfReadings = listOfEntries.stream()
                .map(dataEntity -> {
                    Reading reading = new Reading();
                    reading.setDate(dataEntity.getMeasurementEntryTime().toString());
                    reading.setUnit(dataEntity.getUnit().getUnitName());
                    reading.setSugarValue(dataEntity.getSugarValue());
                    reading.setContext(dataEntity.getValue());
                    return reading;
                })
                .toList();

        Reading latestReading = listOfReadings.getFirst();
        List<Reading> listOfLastWeekReadings = listOfReadings.stream().filter(a -> LocalDateTime.parse(a.getDate()).isAfter(oneWeekAgo)).toList();
        weeklyAverageDouble = listOfLastWeekReadings.stream().mapToDouble(Reading::getSugarValue).average().orElse(0.0);
        LatestReadings latestReadings = new LatestReadings();
        WeeklyAverage weeklyAverage = new WeeklyAverage();
        double weeklyAverageDoubleRounded = new BigDecimal(weeklyAverageDouble).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        weeklyAverage.setValue(weeklyAverageDoubleRounded);
        weeklyAverage.setUnit(SugarUnit.getLabelById(userEntity.getUnitId()));
        weeklyAverage.setStatus(calculateStatus(weeklyAverageDouble, weeklyAverage.getUnit()));
        latestReadings.setReading(latestReading);
        latestReadings.setWeeklyAverage(weeklyAverage);
        WeeklyOverview weeklyOverview = new WeeklyOverview();
        weeklyOverview.setReadings(listOfLastWeekReadings);
        dashBoardData.setLatestReadings(latestReadings);
        dashBoardData.setWeeklyOverview(weeklyOverview);

        List<MedicationEntity> medicationEntityList = medicationRepository.findAllMedicationsByUserId(userId);
        List<Medication> listOfMedications = medicationEntityList.stream().map(medEntity ->
        {
            Medication medication = new Medication();
            medication.setName(medEntity.getMedicationName());
            return medication;
        }).toList();

        dashBoardData.setMedications(listOfMedications);

        return dashBoardData;
    }

    private String calculateStatus(double sugarValue, String sugarUnit) {

        switch (sugarUnit) {
            case "mmol/L":

                if (sugarValue < 3.9) {
                    return "low";
                } else if (sugarValue >= 3.9 && sugarValue <= 5.5) {
                    return "normal";
                } else if (sugarValue >= 5.6 && sugarValue <= 6.9) {
                    return "elevated";
                } else {
                    return "high";
                }
            case "mg/dL":

                if (sugarValue < 70) {
                    return "low";
                } else if (sugarValue >= 70 && sugarValue <= 99) {
                    return "normal";
                } else if (sugarValue >= 100 && sugarValue <= 125) {
                    return "elevated";
                } else {
                    return "high";
                }
            default:
                return "Invalid unit";
        }
    }
}

