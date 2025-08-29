package ch.g24.api.services;

import ch.g24.api.models.*;
import ch.g24.api.repository.entities.DataEntity;
import ch.g24.api.repository.entities.MedicationEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.MedicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Service
public class AnalysisService {

    @Value("${analisys-ai-url}")
    private String analisys_url;

    private final DataRepository dataRepository;
    private final MedicationRepository medicationRepository;

    public AnalysisService(DataRepository dataRepository, MedicationRepository medicationRepository) {
        this.dataRepository = dataRepository;
        this.medicationRepository = medicationRepository;
    }

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

    public DashBoardData getDashboard(long userId) {
        List<DataEntity> listOfEntries = dataRepository.getDataByUserId(userId).stream().sorted(Comparator.comparing(DataEntity::getMeasurementEntryTime).reversed()).toList();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        DashBoardData dashBoardData = new DashBoardData();
        double weeklyAverageDouble;

        if (listOfEntries.isEmpty())
            throw new RuntimeException("No User found");

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
        List<Reading> listOfLastWeekReadings = listOfReadings.stream().filter(a->LocalDateTime.parse(a.getDate()).isAfter(oneWeekAgo)).toList();
        weeklyAverageDouble = listOfLastWeekReadings.stream().mapToDouble(Reading::getSugarValue).average().orElse(0.0);
        LatestReadings latestReadings = new LatestReadings();
        WeeklyAverage weeklyAverage = new WeeklyAverage();
        weeklyAverage.setValue(weeklyAverageDouble);
        weeklyAverage.setUnit("mg/dL");
        weeklyAverage.setStatus("Normal");
        latestReadings.setReading(latestReading);
        latestReadings.setWeeklyAverage(weeklyAverage);
        WeeklyOverview weeklyOverview = new WeeklyOverview();
        weeklyOverview.setReadings(listOfLastWeekReadings);
        dashBoardData.setLatestReadings(latestReadings);
        dashBoardData.setWeeklyOverview(weeklyOverview);


        List<MedicationEntity> medicationEntityList = medicationRepository.findAllMedicationsByUserId(userId);

        List<Medication> listOfMedications = medicationEntityList.stream().map(medEntity->
        {
            Medication medication = new Medication();
            medication.setName(medEntity.getMedicationName());
            return medication;
        }).toList();

       dashBoardData.setMedications(listOfMedications);

        return dashBoardData;
    }
}
