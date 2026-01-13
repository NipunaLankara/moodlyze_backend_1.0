package com.example.emotion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmotionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmotionServiceApplication.class, args);
	}

}
