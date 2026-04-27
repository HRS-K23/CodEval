package com.codeval.dsagenerator.service;

import com.codeval.dsagenerator.dto.GeneratedProblemResponse;
import com.codeval.dsagenerator.model.Category;
import com.codeval.dsagenerator.model.Difficulty;

public interface ProblemGenerationService {

    GeneratedProblemResponse generate(Difficulty difficulty, Category category, String sessionId);
}
