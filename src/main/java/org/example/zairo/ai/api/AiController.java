package org.example.zairo.ai.api;

import org.example.zairo.ai.application.dto.FinancialData;
import org.example.zairo.ai.application.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @GetMapping("/summary")
    public ResponseEntity<FinancialData> getSummary() {
        FinancialData data = aiService.getFinancialDataSummary();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askZai(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String zaiResponse = aiService.askZai(prompt);
        return ResponseEntity.ok(Map.of("response", zaiResponse));
    }
}