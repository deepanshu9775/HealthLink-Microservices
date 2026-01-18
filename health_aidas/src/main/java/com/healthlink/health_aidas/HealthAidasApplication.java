package com.healthlink.health_aidas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = {MongoAutoConfiguration.class}) // Ye line localhost error rok degi
public class HealthAidasApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthAidasApplication.class, args);
	}

}
