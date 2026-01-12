package com.example.emotion_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "emotion_records")
public class EmotionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int userId;

    private String emotion;   // HAPPY, SAD, STRESSED

    private String source;    // IMAGE or SPEECH

    private LocalDateTime detectedAt;
}
