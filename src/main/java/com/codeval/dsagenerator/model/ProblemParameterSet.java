package com.codeval.dsagenerator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "problem_parameter_set")
public class ProblemParameterSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ProblemTemplate template;

    @Column(nullable = false, length = 100)
    private String paramKey;

    @Column(nullable = false, length = 255)
    private String paramValueRange;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProblemTemplate getTemplate() { return template; }
    public void setTemplate(ProblemTemplate template) { this.template = template; }

    public String getParamKey() { return paramKey; }
    public void setParamKey(String paramKey) { this.paramKey = paramKey; }

    public String getParamValueRange() { return paramValueRange; }
    public void setParamValueRange(String paramValueRange) { this.paramValueRange = paramValueRange; }
}
