package com.healthlink.health_aidas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String ask(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // --- EDIT START: Gemini ko batana ki wo kaun hai ---
        String systemInstruction = "Your name is Deep. You are a highly specialized Health Assistant. " +
                "Answer queries as Deep. Never mention you are a language model from Google. ";

        // Asli message mein instruction jod do
        String finalPrompt = systemInstruction + " User query: " + userMessage;
        // --- EDIT END ---

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", finalPrompt); // Yahan finalPrompt jayega

        Map<String, Object> partContainer = new HashMap<>();
        partContainer.put("parts", List.of(textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(partContainer));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            String fullUrl = apiUrl + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, entity, Map.class);

            List candidates = (List) response.getBody().get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);

            return firstPart.get("text").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Gemini Error: " + e.getMessage();
        }
    }
}





//connection of open ai
//@Service
//public class OpenAIService {
//
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    @Value("${openai.api.url}")
//    private String apiUrl;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String ask(String prompt) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        String body = """
//        {
//          "model": "gpt-4.1-mini",
//          "messages": [
//            {"role": "user", "content": "%s"}
//          ]
//        }
//        """.formatted(prompt);
//
//        HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response =
//                restTemplate.postForEntity(apiUrl, entity, String.class);
//
//        return response.getBody();
//    }
//}