package ch.g24.api.controller;

import ch.g24.api.models.*;
import ch.g24.api.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class G24Controller {


    private final UserService userService;
    private final DataService dataService;
    private final AnalysisService analysisService;
    private final DeepSeekService deepSeekService;
    private final EmailService emailService;

    public G24Controller(UserService userService, DataService dataService, AnalysisService analysisService, DeepSeekService deepSeekService, EmailService emailService) {
        this.userService = userService;
        this.dataService = dataService;
        this.analysisService = analysisService;
        this.deepSeekService = deepSeekService;
        this.emailService = emailService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        boolean success = userService.saveUser(user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User successfully registered"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to register user"));
        }
    }

    @PostMapping("/first-registration")
    public ResponseEntity<Map<String, String>> first_registerUser(@RequestBody User user) {

        boolean success = userService.saveUser(user);
        if (success) {
            emailService.sendWelcomeEmail(user.email(), user.password());
            return ResponseEntity.ok(Map.of("message", "User successfully registered"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to register user"));
        }
    }

    @PostMapping("/addEntry")
    public ResponseEntity<Map<String, String>> addEntry(@RequestBody Entry entry) {

        boolean isAdded = dataService.addEntry(entry);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message", "Entry successfully added"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to add entry"));
        }
    }

    @GetMapping("/getdata/{userId}")
    public ResponseEntity<List<Data>> getData(@PathVariable Long userId) {
        List<Data> dataList = dataService.getData(userId);

        if (dataList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(dataList);
        }
    }

    @PostMapping("/getanalysis")
    public ResponseEntity<String> getAnalysis(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(analysisService.forwardPdfToDeepSeek(file));
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashBoardData> getDashboardData(@PathVariable Long userId) throws Exception {
        DashBoardData dashboard = analysisService.getDashboard(userId);

        List<Reading> readingList = dashboard.getWeeklyOverview().getReadings();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(readingList);
        Map<String, Object> map = new HashMap<>();
        map.put("readings", json);
        DeepSeekResult deepSeekResult = analyzePatientData(map);
        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setHbA1cPrediction(deepSeekResult.getAi_analysis().getHbA1cPrediction());
        aiAnalysis.setSummary(deepSeekResult.getAi_analysis().getSummary());
        dashboard.setAiAnalysis(deepSeekResult.getAi_analysis());
        dashboard.setSmartInsight(deepSeekResult.getSmart_insight());

        return ResponseEntity.ok(dashboard);
    }

    public DeepSeekResult analyzePatientData(Map<String, Object> patientData) throws Exception {
        return deepSeekService.getAiAnalysis(patientData);
    }

}
