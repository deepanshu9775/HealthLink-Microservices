package com.healthlink.health_aidas.controller;

import com.healthlink.health_aidas.service.EmbeddingGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private EmbeddingGenerationService embeddingGenService;

    @PostMapping("/generate-embeddings")
    public String generateEmbeddings() {
        try {
            embeddingGenService.generateEmbeddingsForAllData();
            return "✅ Embeddings generated successfully!";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}