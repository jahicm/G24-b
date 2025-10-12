package ch.g24.api.services;

import ch.g24.api.models.DeepSeekResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeepSeekServiceTest {


    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DeepSeekService deepSeekService;


    @BeforeEach
    void setup() {

        ReflectionTestUtils.setField(deepSeekService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(deepSeekService, "DEEPSEEK_URL", "https://fake-url.com");
        ReflectionTestUtils.setField(deepSeekService, "DEEPSEEK_API_KEY", "test-key");
    }
    @Test
    void getAiAnalysis_shouldReturnParsedResult() throws Exception {
        Map<String, Object> patientData = Map.of("lang", "en");

        Map<String, Object> mockMessage = Map.of(
                "content", "{ \"ai_analysis\": { \"summary\": { \"weekly_avg\": 5.4 } } }"
        );
        Map<String, Object> mockChoice = Map.of("message", mockMessage);
        Map<String, Object> mockResponse = Map.of("choices", List.of(mockChoice));

        // ✅ All arguments are matchers
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        DeepSeekResult result = deepSeekService.getAiAnalysis(patientData);

        assertEquals(5.4, result.getAi_analysis().getSummary().getWeekly_avg());
    }

}
