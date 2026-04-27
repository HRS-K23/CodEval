package com.codeval.dsagenerator.service;

import com.codeval.dsagenerator.model.Difficulty;
import org.springframework.stereotype.Component;

@Component
public class DifficultyCalibrator {

    public Difficulty calibrate(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Difficulty must not be blank.");
        }
        try {
            return Difficulty.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid difficulty '" + raw + "'. Accepted values: EASY, MEDIUM, HARD.");
        }
    }
}
