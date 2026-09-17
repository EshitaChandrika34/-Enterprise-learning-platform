package com.enterpriselearning.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CareerAiController {

    private final CareerAiService careerAiService;

    @PostMapping("/career-chat")
    public ResponseEntity<CareerAiResponse> careerChat(
            @RequestBody CareerAiRequest request) {

        CareerAiResponse response =
                careerAiService.generateCareerAdvice(request);

        return ResponseEntity.ok(response);
    }
}