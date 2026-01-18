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
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> generateEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ CORRECT FORMAT FOR GEMINI EMBEDDING API
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", text);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "models/text-embedding-004"); // Updated model
        body.put("content", content);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent?key=" + apiKey;

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("embedding")) {
                throw new RuntimeException("Invalid response from Gemini API");
            }

            Map<String, Object> embedding = (Map<String, Object>) response.getBody().get("embedding");
            List<Number> values = (List<Number>) embedding.get("values");

            return values.stream()
                    .map(Number::doubleValue)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("❌ Error generating embedding: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage());
        }
    }
}