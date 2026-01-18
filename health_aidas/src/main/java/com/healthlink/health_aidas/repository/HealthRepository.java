package com.healthlink.health_aidas.repository;

import com.healthlink.health_aidas.entity.HealthData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HealthRepository extends MongoRepository<HealthData,String> {
    // Heart data ke liye
    @Query("{ 'Cholesterol' : { $gt: ?0, $lt: ?1 } }")
    List<HealthData> findByCholesterolBetween(int min, int max);

    // Lung data ke liye (Lung Capacity ke basis par)
    @Query("{ 'Lung Capacity' : { $gt: ?0, $lt: ?1 } }")
    List<HealthData> findByLungCapacityBetween(double min, double max);
    // 3. Category ke hisaab se saara data nikalne ke liye
    //List<HealthData> findByRecordType(String recordType);
}
