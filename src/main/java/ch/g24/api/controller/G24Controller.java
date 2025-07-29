package ch.g24.api.controller;

import ch.g24.api.models.Data;
import ch.g24.api.models.Entry;
import ch.g24.api.models.User;
import ch.g24.api.services.AnalysisService;
import ch.g24.api.services.DataService;
import ch.g24.api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class G24Controller {


    private final UserService userService;
    private final DataService dataService;
    private final AnalysisService analysisService;

    public G24Controller(UserService userService, DataService dataService, AnalysisService analysisService) {
        this.userService = userService;
        this.dataService = dataService;
        this.analysisService = analysisService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.getUser(Long.parseLong(userId)));
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


    @PostMapping("/addEntry")
    public ResponseEntity<Map<String, String>> addEntry(@RequestBody Entry entry) {

        boolean isAdded = dataService.addEntry(entry);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message","Entry successfully added"));
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

    @PostMapping("/getanalisys")
    public ResponseEntity<String> getanalisys(@RequestParam("file") MultipartFile file) {

        return analysisService.forwardFileToAIServer(file);
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<String> getDashboardData(@PathVariable Long userId) {
        String dashboard = analysisService.getDashboard(userId);
        if (dashboard.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(dashboard);
        }
    }
}
