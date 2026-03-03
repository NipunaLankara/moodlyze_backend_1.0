package com.example.emotion_service.repo;

import com.example.emotion_service.entity.EmotionRecord;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface EmotionRepo extends JpaRepository<EmotionRecord,Long> {

    // Finds the top 1 record for a user, ordered by date descending
    Optional<EmotionRecord> findTopByUserIdOrderByDetectedAtDesc(int userId);
}
