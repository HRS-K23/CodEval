package com.codeval.dsagenerator.repository;

import com.codeval.dsagenerator.model.GenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationLogRepository extends JpaRepository<GenerationLog, Long> {

    List<Long> findTemplateIdBySessionId(String sessionId);
}
