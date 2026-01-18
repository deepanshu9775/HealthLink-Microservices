package com.healthlink.health_aidas.service;

import com.healthlink.health_aidas.entity.HealthData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EmbeddingService embeddingService;

    public String getSmartResponse(String userQuery, boolean isFirst, String username,
                                   Double healthValue, String category) {

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔍 RAG Vector Search Started");
        System.out.println("📝 Query: " + userQuery);
        System.out.println("👤 User: " + username);
        System.out.println("=".repeat(60));

        try {
            // STEP 1: Generate query embedding
            List<Double> queryEmbedding = embeddingService.generateEmbedding(userQuery);
            System.out.println("✅ Query embedding generated: " + queryEmbedding.size() + " dimensions");

            // STEP 2: Retrieve all documents from MongoDB
            List<HealthData> allData = mongoTemplate.findAll(HealthData.class, "disease");
            System.out.println("✅ Retrieved " + allData.size() + " documents from database");

            // STEP 3: Calculate similarity scores
            List<HealthDataWithScore> scoredData = new ArrayList<>();
            int validEmbeddings = 0;

            for (HealthData data : allData) {
                if (data.getEmbedding() != null && !data.getEmbedding().isEmpty()) {
                    double similarity = cosineSimilarity(queryEmbedding, data.getEmbedding());
                    scoredData.add(new HealthDataWithScore(data, similarity));
                    validEmbeddings++;
                }
            }

            System.out.println("✅ Calculated similarity for " + validEmbeddings + " records");

            if (scoredData.isEmpty()) {
                System.err.println("⚠️  No embeddings found! Run /api/admin/generate-embeddings first");
                return "⚠️ Database not ready. Please ask admin to generate embeddings first.";
            }

            // STEP 4: Get top 5 most similar cases
            List<HealthData> topMatches = scoredData.stream()
                    .sorted(Comparator.comparingDouble(HealthDataWithScore::getScore).reversed())
                    .limit(5)
                    .map(HealthDataWithScore::getData)
                    .collect(Collectors.toList());

            System.out.println("✅ Top " + topMatches.size() + " similar cases retrieved");

            // Log similarity scores for debugging
            for (int i = 0; i < Math.min(3, topMatches.size()); i++) {
                HealthDataWithScore match = scoredData.get(i);
                System.out.println("   Match " + (i+1) + " - Similarity: " +
                        String.format("%.4f", match.getScore()));
            }

            // STEP 5: Build context from top matches
            StringBuilder context = new StringBuilder();
            context.append("RELEVANT MEDICAL CASES FROM DATABASE:\n\n");

            for (int i = 0; i < topMatches.size(); i++) {
                HealthData data = topMatches.get(i);
                context.append("Case ").append(i + 1).append(": ");

                if (data.getAge() != null) {
                    context.append("Age ").append(data.getAge());
                }

                if (data.getCholesterol() != null) {
                    context.append(", Cholesterol ").append(data.getCholesterol()).append(" mg/dL");
                }

                if (data.getHeartDiseaseStatus() != null) {
                    context.append(", Heart Disease: ").append(data.getHeartDiseaseStatus());
                }

                if (data.getLungDiseaseType() != null) {
                    context.append(", Lung Disease: ").append(data.getLungDiseaseType());
                }

                if (data.getSmokingStatus() != null) {
                    context.append(", Smoking: ").append(data.getSmokingStatus());
                }

                if (data.getLungCapacity() != null) {
                    context.append(", Lung Capacity: ").append(data.getLungCapacity()).append("L");
                }

                if (data.getRecovered() != null) {
                    context.append(", Recovery: ").append(data.getRecovered());
                }

                context.append("\n");
            }

            // STEP 6: Create enhanced prompt
            String systemContext = "You are Deep, a medical AI assistant with access to a health database. " +
                    "Analyze the similar cases provided and give helpful, evidence-based medical guidance. " +
                    "Be empathetic but professional. Always remind users to consult real doctors for diagnosis.\n\n";

            String finalPrompt = systemContext + context.toString() +
                    "\nPATIENT QUESTION: " + userQuery +
                    "\n\nProvide a helpful response based on the similar cases above:";

            System.out.println("✅ Sending enriched prompt to Gemini...");

            // STEP 7: Get AI response
            String aiAnswer = geminiService.ask(finalPrompt);

            System.out.println("✅ Response generated successfully");
            System.out.println("=".repeat(60) + "\n");

            return isFirst ? "Hello " + username + "! 👋\n\n" + aiAnswer : aiAnswer;

        } catch (Exception e) {
            System.err.println("❌ Error in RAG pipeline: " + e.getMessage());
            e.printStackTrace();
            return "Sorry, I encountered an error processing your request. Please try again.";
        }
    }

    /**
     * Calculate cosine similarity between two embedding vectors
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1 == null || vec2 == null || vec1.size() != vec2.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            double v1 = vec1.get(i);
            double v2 = vec2.get(i);

            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Helper class to store health data with similarity score
     */
    private static class HealthDataWithScore {
        private final HealthData data;
        private final double score;

        public HealthDataWithScore(HealthData data, double score) {
            this.data = data;
            this.score = score;
        }

        public HealthData getData() {
            return data;
        }

        public double getScore() {
            return score;
        }
    }
}