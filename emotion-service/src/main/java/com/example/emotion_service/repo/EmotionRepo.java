package com.example.emotion_service.repo;

import com.example.emotion_service.entity.EmotionRecord;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface EmotionRepo extends JpaRepository<EmotionRecord,Long> {
}
