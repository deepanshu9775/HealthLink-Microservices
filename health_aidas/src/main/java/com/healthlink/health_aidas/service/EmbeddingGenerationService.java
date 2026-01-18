    package com.healthlink.health_aidas.service;

    import com.healthlink.health_aidas.entity.HealthData;
    import com.healthlink.health_aidas.repository.HealthRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class EmbeddingGenerationService {

        @Autowired
        private HealthRepository healthRepository;

        @Autowired
        private EmbeddingService embeddingService;

        public void generateEmbeddingsForAllData() {
            List<HealthData> allData = healthRepository.findAll();

            System.out.println("🚀 Starting embedding generation for " + allData.size() + " records...");

            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < allData.size(); i++) {
                HealthData data = allData.get(i);

                try {
                    // Skip if embedding already exists
                    if (data.getEmbedding() != null && !data.getEmbedding().isEmpty()) {
                        System.out.println("⏭️  Skipping record " + (i + 1) + " (embedding exists)");
                        continue;
                    }

                    String combinedText = buildCombinedText(data);

                    // Generate embedding
                    List<Double> embedding = embeddingService.generateEmbedding(combinedText);

                    // Save to database
                    data.setCombinedText(combinedText);
                    data.setEmbedding(embedding);
                    healthRepository.save(data);

                    successCount++;
                    System.out.println("✅ Progress: " + (i + 1) + "/" + allData.size() + " | Success: " + successCount);

                    // Sleep to avoid rate limiting (Gemini: 60 requests/min for free tier)
                    Thread.sleep(1100); // 1.1 seconds = safe for 60/min limit

                } catch (Exception e) {
                    failCount++;
                    System.err.println("❌ Failed for record " + (i + 1) + ": " + e.getMessage());

                    // If rate limit error, wait longer
                    if (e.getMessage() != null && e.getMessage().contains("429")) {
                        System.out.println("⏸️  Rate limit hit. Waiting 60 seconds...");
                        try {
                            Thread.sleep(60000); // Wait 1 minute
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                }
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("✅ Embedding generation complete!");
            System.out.println("📊 Success: " + successCount + " | Failed: " + failCount);
            System.out.println("=".repeat(50));
        }

        private String buildCombinedText(HealthData data) {
            StringBuilder text = new StringBuilder();

            if (data.getAge() != null) {
                text.append("Patient age ").append(data.getAge()).append(" years. ");
            }

            if (data.getCholesterol() != null) {
                text.append("Cholesterol level ").append(data.getCholesterol()).append(" mg/dL. ");
            }

            if (data.getChestPainType() != null) {
                text.append("Chest pain type ").append(data.getChestPainType()).append(". ");
            }

            if (data.getHeartDiseaseStatus() != null) {
                text.append("Heart disease status: ").append(data.getHeartDiseaseStatus()).append(". ");
            }

            if (data.getSmokingStatus() != null) {
                text.append("Smoking status: ").append(data.getSmokingStatus()).append(". ");
            }

            if (data.getLungCapacity() != null) {
                text.append("Lung capacity ").append(data.getLungCapacity()).append(" liters. ");
            }

            if (data.getLungDiseaseType() != null) {
                text.append("Lung disease type: ").append(data.getLungDiseaseType()).append(". ");
            }

            if (data.getRecovered() != null) {
                text.append("Recovery status: ").append(data.getRecovered()).append(". ");
            }

            return text.toString().trim();
        }
    }