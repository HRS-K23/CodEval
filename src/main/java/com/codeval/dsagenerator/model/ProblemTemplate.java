package com.codeval.dsagenerator.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "problem_template")
public class ProblemTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String templateBody;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(length = 50)
    private String expectedComplexity;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProblemParameterSet> parameterSets;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTemplateBody() { return templateBody; }
    public void setTemplateBody(String templateBody) { this.templateBody = templateBody; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getExpectedComplexity() { return expectedComplexity; }
    public void setExpectedComplexity(String expectedComplexity) { this.expectedComplexity = expectedComplexity; }

    public List<ProblemParameterSet> getParameterSets() { return parameterSets; }
    public void setParameterSets(List<ProblemParameterSet> parameterSets) { this.parameterSets = parameterSets; }
}
