package com.healthlink.health_aidas.controller;

import com.healthlink.health_aidas.service.GeminiService;
import com.healthlink.health_aidas.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//import com.healthlink.health_aidas.service.OpenAIService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/ai")
public class AIController {


    @Autowired
    private GeminiService geminiService;
    @Autowired
    private HealthService healthService;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, Object> payload) {
        String userMessage = (String) payload.get("message");
        String username = (String) payload.get("username");
        boolean isFirst = payload.get("isFirst") != null && (boolean) payload.get("isFirst");

        // Sabse important change: Isko Double mein cast karo
        Double healthValue = null;
        if (payload.get("value") != null) {
            healthValue = Double.valueOf(payload.get("value").toString());
        }

        String category = payload.get("category") != null ? payload.get("category").toString() : "GENERAL";

        // Ab parameters match ho jayenge: (String, boolean, String, Double, String)
        return healthService.getSmartResponse(userMessage, isFirst, username, healthValue, category);
    }
}

//    @Autowired
//    private OpenAIService openAIService;
//
//    @PostMapping("/chat")
//    public String chat(@RequestBody ChatRequest request) {
//        return openAIService.ask(request.getMessage());
//    }
//}

class ChatRequest {
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}