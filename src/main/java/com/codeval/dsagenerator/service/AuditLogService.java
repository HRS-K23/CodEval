package com.codeval.dsagenerator.service;

import com.codeval.dsagenerator.model.GenerationLog;
import com.codeval.dsagenerator.model.Difficulty;
import com.codeval.dsagenerator.repository.GenerationLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {

    private final GenerationLogRepository generationLogRepository;

    public AuditLogService(GenerationLogRepository generationLogRepository) {
        this.generationLogRepository = generationLogRepository;
    }

    @Async
    public void log(String sessionId, Long templateId, Difficulty difficulty) {
        GenerationLog entry = new GenerationLog();
        entry.setSessionId(sessionId != null ? sessionId : "anonymous");
        entry.setTemplateId(templateId);
        entry.setDifficulty(difficulty);
        entry.setGeneratedAt(Instant.now());
        generationLogRepository.save(entry);
    }
}
