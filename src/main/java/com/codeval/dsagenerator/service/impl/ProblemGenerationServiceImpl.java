package com.codeval.dsagenerator.service.impl;

import com.codeval.dsagenerator.dto.GeneratedProblemResponse;
import com.codeval.dsagenerator.exception.NoProblemsAvailableException;
import com.codeval.dsagenerator.model.Category;
import com.codeval.dsagenerator.model.Difficulty;
import com.codeval.dsagenerator.model.ProblemTemplate;
import com.codeval.dsagenerator.repository.ProblemTemplateRepository;
import com.codeval.dsagenerator.service.AuditLogService;
import com.codeval.dsagenerator.service.DeduplicationFilter;
import com.codeval.dsagenerator.service.ProblemGenerationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ProblemGenerationServiceImpl implements ProblemGenerationService {

    private final ProblemTemplateRepository templateRepository;
    private final DeduplicationFilter deduplicationFilter;
    private final AuditLogService auditLogService;
    private final Random random = new Random();

    public ProblemGenerationServiceImpl(ProblemTemplateRepository templateRepository,
                                        DeduplicationFilter deduplicationFilter,
                                        AuditLogService auditLogService) {
        this.templateRepository = templateRepository;
        this.deduplicationFilter = deduplicationFilter;
        this.auditLogService = auditLogService;
    }

    @Override
    public GeneratedProblemResponse generate(Difficulty difficulty, Category category, String sessionId) {
        List<ProblemTemplate> candidates = fetchCandidates(difficulty, category);

        if (candidates.isEmpty()) {
            throw new NoProblemsAvailableException(
                "No templates available for difficulty=" + difficulty +
                (category != null ? " category=" + category : ""));
        }

        List<ProblemTemplate> filtered = deduplicationFilter.filter(candidates, sessionId);
        ProblemTemplate selected = filtered.get(random.nextInt(filtered.size()));

        String description = parameterise(selected.getTemplateBody());
        String problemId = "template-" + selected.getId() +
                           (sessionId != null ? "-session-" + sessionId : "");

        auditLogService.log(sessionId, selected.getId(), difficulty);

        return new GeneratedProblemResponse(
            problemId,
            selected.getTitle(),
            selected.getDifficulty(),
            selected.getCategory(),
            description,
            selected.getConstraints(),
            selected.getExpectedComplexity()
        );
    }

    @Cacheable(value = "templates", key = "#difficulty.name() + '_' + (#category != null ? #category.name() : 'ALL')")
    public List<ProblemTemplate> fetchCandidates(Difficulty difficulty, Category category) {
        if (category != null) {
            return templateRepository.findByDifficultyAndCategory(difficulty, category);
        }
        return templateRepository.findByDifficulty(difficulty);
    }

    private String parameterise(String templateBody) {
        // Replace numeric placeholders with random values within reasonable bounds
        return templateBody
            .replaceAll("\\{n\\}", String.valueOf(10 + random.nextInt(91)))
            .replaceAll("\\{max\\}", String.valueOf(100 + random.nextInt(901)))
            .replaceAll("\\{min\\}", String.valueOf(random.nextInt(10)));
    }
}
