package com.codeval.dsagenerator.controller;

import com.codeval.dsagenerator.dto.GeneratedProblemResponse;
import com.codeval.dsagenerator.model.Category;
import com.codeval.dsagenerator.model.Difficulty;
import com.codeval.dsagenerator.service.DifficultyCalibrator;
import com.codeval.dsagenerator.service.ProblemGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

    private final ProblemGenerationService problemGenerationService;
    private final DifficultyCalibrator difficultyCalibrator;

    public ProblemController(ProblemGenerationService problemGenerationService,
                             DifficultyCalibrator difficultyCalibrator) {
        this.problemGenerationService = problemGenerationService;
        this.difficultyCalibrator = difficultyCalibrator;
    }

    @GetMapping("/generate")
    public ResponseEntity<GeneratedProblemResponse> generate(
            @RequestParam String difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sessionId) {

        if (sessionId != null && sessionId.length() > 255) {
            throw new IllegalArgumentException("sessionId must not exceed 255 characters.");
        }

        Difficulty calibrated = difficultyCalibrator.calibrate(difficulty);
        Category parsedCategory = parseCategory(category);

        GeneratedProblemResponse response =
            problemGenerationService.generate(calibrated, parsedCategory, sessionId);

        return ResponseEntity.ok(response);
    }

    private Category parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Category.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid category '" + raw + "'. Accepted values: ARRAYS, TREES, GRAPHS, DP, STRINGS, SORTING.");
        }
    }
}
