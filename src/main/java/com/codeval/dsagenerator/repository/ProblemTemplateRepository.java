package com.codeval.dsagenerator.repository;

import com.codeval.dsagenerator.model.Difficulty;
import com.codeval.dsagenerator.model.Category;
import com.codeval.dsagenerator.model.ProblemTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemTemplateRepository extends JpaRepository<ProblemTemplate, Long> {

    List<ProblemTemplate> findByDifficulty(Difficulty difficulty);

    List<ProblemTemplate> findByDifficultyAndCategory(Difficulty difficulty, Category category);
}
