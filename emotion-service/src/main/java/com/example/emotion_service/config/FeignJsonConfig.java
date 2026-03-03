package com.example.emotion_service.config;

import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignJsonConfig {

    @Bean
    @Primary
    public Encoder feignEncoder() {
        return new SpringEncoder(() -> new HttpMessageConverters());
    }
}