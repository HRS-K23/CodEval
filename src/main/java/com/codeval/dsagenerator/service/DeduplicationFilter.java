package com.codeval.dsagenerator.service;

import com.codeval.dsagenerator.model.ProblemTemplate;
import com.codeval.dsagenerator.repository.GenerationLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeduplicationFilter {

    private final GenerationLogRepository generationLogRepository;

    public DeduplicationFilter(GenerationLogRepository generationLogRepository) {
        this.generationLogRepository = generationLogRepository;
    }

    public List<ProblemTemplate> filter(List<ProblemTemplate> candidates, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return candidates;
        }
        List<Long> seen = generationLogRepository.findTemplateIdBySessionId(sessionId);
        if (seen.isEmpty()) {
            return candidates;
        }
        List<ProblemTemplate> filtered = candidates.stream()
            .filter(t -> !seen.contains(t.getId()))
            .collect(Collectors.toList());
        // If all templates have been seen, reset deduplication for this session
        return filtered.isEmpty() ? candidates : filtered;
    }
}
