package com.codeval.dsagenerator.dto;

import com.codeval.dsagenerator.model.Category;
import com.codeval.dsagenerator.model.Difficulty;

public class GeneratedProblemResponse {

    private String problemId;
    private String title;
    private Difficulty difficulty;
    private Category category;
    private String description;
    private String constraints;
    private String expectedComplexity;

    public GeneratedProblemResponse() {}

    public GeneratedProblemResponse(String problemId, String title, Difficulty difficulty,
                                    Category category, String description,
                                    String constraints, String expectedComplexity) {
        this.problemId = problemId;
        this.title = title;
        this.difficulty = difficulty;
        this.category = category;
        this.description = description;
        this.constraints = constraints;
        this.expectedComplexity = expectedComplexity;
    }

    public String getProblemId() { return problemId; }
    public void setProblemId(String problemId) { this.problemId = problemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getExpectedComplexity() { return expectedComplexity; }
    public void setExpectedComplexity(String expectedComplexity) { this.expectedComplexity = expectedComplexity; }
}
