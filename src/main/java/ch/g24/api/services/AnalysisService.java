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

import static ch.g24.api.utility.Utility.convertSugar;


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
        String jsonString = rawContent.toString().replaceAll("(?s)^```json\\s*(.*)\\s*```$", "$1").trim();

        if (rawContent instanceof String) {
            analysis = objectMapper.readValue((String) jsonString, Map.class);
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
        String unitId = userEntity.getUnitId();

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        DashBoardData dashBoardData = new DashBoardData();
        double weeklyAverageDouble;

        if (listOfEntries.isEmpty()) {

            Reading reading = new Reading();
            List<Reading> listOfLastWeekReadings = new ArrayList<>();
            listOfLastWeekReadings.add(reading);
            WeeklyOverview weeklyOverview = new WeeklyOverview();
            weeklyOverview.setReadings(listOfLastWeekReadings);
            dashBoardData.setWeeklyOverview(weeklyOverview);
            return dashBoardData;
        }
        List<Reading> listOfReadings = listOfEntries.stream()
                .map(dataEntity -> {
                    Reading reading = new Reading();
                    reading.setDate(dataEntity.getMeasurementEntryTime().toString());
                    reading.setUnit(SugarUnit.getLabelById(String.valueOf(dataEntity.getUnit().getUnitId())));
                    reading.setSugarValue(dataEntity.getSugarValue());
                    reading.setContext(dataEntity.getValue());
                    return reading;
                })
                .toList();

        Reading latestReading = listOfReadings.getFirst();
        double latestVal = convertSugar(latestReading.getSugarValue(), unitId, latestReading.getUnit());
        latestReading.setSugarValue(latestVal);
        latestReading.setUnit(SugarUnit.getLabelById(unitId));

        List<Reading> listOfLastWeekReadings = listOfReadings.stream()
                .filter(r -> LocalDateTime.parse(r.getDate()).isAfter(oneWeekAgo))
                .map(r -> {
                    String unit = r.getUnit();
                    double val = convertSugar(r.getSugarValue(), unitId, unit);
                    Reading reading = new Reading();
                    reading.setUnit(unit);
                    reading.setDate(r.getDate());
                    reading.setSugarValue(val);
                    return reading;
                })
                .toList();

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

        Optional<MedicationEntity> medicationEntity = medicationRepository.findAllMedicationsByUserId(userId);
        List<Medication> medications = new ArrayList<>();
        medicationEntity.ifPresent(m -> {
            Medication medication = new Medication();
            medication.setMedicationId(medicationEntity.get().getMedicationId());
            medication.setName(medicationEntity.get().getMedicationName());
            medications.add(medication);
        });

        dashBoardData.setMedications(medications);

        return dashBoardData;
    }

    private String calculateStatus(double sugarValue, String sugarUnit) {

        switch (sugarUnit) {
            case "mmol/L":

                if (sugarValue < 3.9) {
                    return "low";
                } else if (sugarValue >= 3.9 && sugarValue <= 7.0) {
                    return "normal";
                } else if (sugarValue >= 7.1 && sugarValue <= 10) {
                    return "elevated";
                } else {
                    return "high";
                }
            case "mg/dL":

                if (sugarValue < 70) {
                    return "low";
                } else if (sugarValue >= 70 && sugarValue <= 130) {
                    return "normal";
                } else if (sugarValue >= 131 && sugarValue <= 179) {
                    return "elevated";
                } else {
                    return "high";
                }
            default:
                return "Invalid unit";
        }
    }
}

