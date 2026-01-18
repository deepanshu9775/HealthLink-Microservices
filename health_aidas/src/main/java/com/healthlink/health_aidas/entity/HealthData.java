package com.healthlink.health_aidas.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "disease")
public class HealthData {

    public void setId(String id) {
        this.id = id;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public void setChestPainType(Integer chestPainType) {
        this.chestPainType = chestPainType;
    }

    public void setCholesterol(Integer cholesterol) {
        this.cholesterol = cholesterol;
    }

    public void setHeartDiseaseStatus(String heartDiseaseStatus) {
        this.heartDiseaseStatus = heartDiseaseStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public void setLungCapacity(Double lungCapacity) {
        this.lungCapacity = lungCapacity;
    }

    public void setLungDiseaseType(String lungDiseaseType) {
        this.lungDiseaseType = lungDiseaseType;
    }

    public void setRecovered(String recovered) {
        Recovered = recovered;
    }

    @Id
    private String id;

    // Common Fields
    @Field("Age")
    private Integer Age; // Database mein 'A' capital hai

    public void setGender(Integer gender) {
        Gender = gender;
    }

    @Override
    public String toString() {
        return "HealthData{" +
                "Gender=" + Gender +
                '}';
    }

    @Field("Sex")
    private Integer Gender;

    public String getId() {
        return id;
    }

    public Integer getAge() {
        return Age;
    }

    public Integer getChestPainType() {
        return chestPainType;
    }

    public Integer getCholesterol() {
        return cholesterol;
    }

    public String getHeartDiseaseStatus() {
        return heartDiseaseStatus;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public Double getLungCapacity() {
        return lungCapacity;
    }

    public String getLungDiseaseType() {
        return lungDiseaseType;
    }

    public String getRecovered() {
        return Recovered;
    }

    // Heart Specific
    @Field("Chest pain type")
    private Integer chestPainType;

    @Field("Cholesterol")
    private Integer cholesterol;

    @Field("Heart Disease")
    private String heartDiseaseStatus;

    // Lung Specific
    @Field("Smoking Status")
    private String smokingStatus;

    @Field("Lung Capacity")
    private Double lungCapacity;

    @Field("Disease Type")
    private String lungDiseaseType;

    @Field("Recovered")
    private String Recovered;

    // ADD THESE TWO NEW FIELDS:
    @Field("embedding")
    private List<Double> embedding;

    @Field("combined_text")
    private String combinedText;

    // ADD THESE GETTERS AND SETTERS:
    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    public String getCombinedText() {
        return combinedText;
    }

    public void setCombinedText(String combinedText) {
        this.combinedText = combinedText;
    }

}