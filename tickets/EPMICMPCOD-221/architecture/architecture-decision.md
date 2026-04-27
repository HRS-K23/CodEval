# DSA Problem Generator — Architecture Decision Document

**Ticket**: EPMICMPCOD-221  
**Status**: APPROVED  
**Date**: 2026-04-24  
**Version**: v1.0  

---

## Executive Summary

The DSA Problem Generator is a dedicated Spring Boot microservice that dynamically generates Data Structure and Algorithm (DSA) problems based on topic and difficulty. It integrates seamlessly with the CodEval platform, providing scalable, high-performance problem generation with caching, validation, and optional AI enhancement.

**Key Architectural Decisions**:
- **Service Model**: Stateless Spring Boot microservice (horizontally scalable)
- **Data Layer**: PostgreSQL + Redis (distributed caching)
- **Scaling**: Kubernetes-based auto-scaling (3-10 pods)
- **Security**: JWT + RBAC + rate limiting
- **Resilience**: Circuit breakers, retry policies, graceful degradation
- **Deployment**: Docker containers, multi-AZ cloud setup

---

## 1. System Overview

```
┌─────────────┐
│   Client    │
│  (Web/App)  │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│   API Gateway       │
│ (Spring Cloud Gw)   │
└──────┬──────────────┘
       │
       ▼
┌──────────────────────────────┐
│  DSA Problem Generator Svc   │
│  (Spring Boot 3.3.4 / Java21)│
└──┬───────────────────────┬───┘
   │                       │
   ▼                       ▼
┌──────────┐         ┌──────────┐
│  Redis   │         │PostgreSQL│
│ (Cache)  │         │(Primary) │
└──────────┘         └──────────┘
   │                       │
   ├─ Problem Templates    ├─ Problem Records
   ├─ Generated Variants   ├─ Topic Catalog
   └─ Session Data         ├─ Difficulty Levels
                           └─ Validation Logs
```

**Core Components**:
1. **API Gateway**: Routes requests, enforces rate limiting, JWT validation
2. **Problem Generator Service**: Core business logic, orchestration
3. **Cache Layer**: Redis for templates and variants (5-30min TTL)
4. **Database**: PostgreSQL with optimized indexes
5. **Validation Service**: Problem quality assurance
6. **AI Engine** (optional): External LLM for enhanced generation

---

## 2. API Design

### Endpoint: Generate Problem(s)
```
POST /api/v1/dsa-problems/generate
```

**Request**:
```json
{
  "topic_id": "uuid",
  "difficulty": "MEDIUM",
  "count": 1,
  "include_solution": false,
  "enable_ai_enhancement": false
}
```

**Success Response** (200):
```json
{
  "status": "success",
  "data": {
    "problems": [
      {
        "id": "uuid",
        "title": "Two Sum",
        "description": "...",
        "topic_name": "Arrays",
        "difficulty": "MEDIUM",
        "test_cases": [...],
        "constraints": {...},
        "estimated_time_minutes": 15
      }
    ],
    "generation_id": "gen-batch-uuid"
  }
}
```

**Error Response** (400/401/429/500):
```json
{
  "status": "error",
  "error_code": "INVALID_TOPIC",
  "message": "Topic not found",
  "timestamp": "2026-04-24T10:30:45Z"
}
```

---

### Endpoint: Batch Generate (Async)
```
POST /api/v1/dsa-problems/batch-generate
```

**Request**:
```json
{
  "requests": [
    {"topic_id": "uuid1", "difficulty": "EASY", "count": 5},
    {"topic_id": "uuid2", "difficulty": "HARD", "count": 3}
  ],
  "callback_url": "https://webhook.example.com/ready"
}
```

**Accepted Response** (202):
```json
{
  "batch_id": "batch-uuid",
  "status": "accepted",
  "estimated_completion_seconds": 120
}
```

---

### Endpoint: List Topics
```
GET /api/v1/dsa-topics
```

**Response** (200):
```json
{
  "topics": [
    {
      "id": "uuid",
      "name": "Arrays",
      "problem_count": 150,
      "difficulty_levels": ["EASY", "MEDIUM", "HARD"]
    }
  ],
  "pagination": {"total_items": 25, "current_page": 0}
}
```

---

## 3. Data Model

### Database Tables

#### `dsa_topics`
```sql
id (UUID, PK)
name (VARCHAR, unique)
description (TEXT)
category (VARCHAR)
icon_url (VARCHAR)
created_at, updated_at (TIMESTAMP)

INDEX: category, name
```

#### `dsa_difficulty_levels`
```sql
id (UUID, PK)
level (VARCHAR, unique) — EASY | MEDIUM | HARD | EXPERT
order_rank (INT, unique)
expected_solve_time_minutes (INT)
success_rate_percentage (DECIMAL)
```

#### `dsa_problem_templates`
```sql
id (UUID, PK)
title (VARCHAR)
description (TEXT)
topic_id (UUID, FK → dsa_topics)
difficulty_id (UUID, FK → dsa_difficulty_levels)
template_content (JSONB)
constraints (JSONB)
tags (VARCHAR[] array)
created_at, updated_at (TIMESTAMP)

INDEX: (topic_id, difficulty_id), tags USING GIN
```

#### `dsa_problems` (Generated Instances)
```sql
id (UUID, PK)
template_id (UUID, FK → dsa_problem_templates)
title (VARCHAR)
description (TEXT)
problem_content (JSONB)
solution_code (TEXT)
solution_explanation (TEXT)
test_cases (JSONB)
complexity_time (VARCHAR)
complexity_space (VARCHAR)
tags (VARCHAR[] array)
difficulty (VARCHAR)
topic_id (UUID, FK → dsa_topics)
generation_id (VARCHAR)
is_cached (BOOLEAN)
cache_expires_at (TIMESTAMP)
created_at (TIMESTAMP)

INDEX: (topic_id, difficulty, created_at DESC)
INDEX: generation_id, template_id, cache_expires_at
```

#### `dsa_problem_validations`
```sql
id (UUID, PK)
problem_id (UUID, FK → dsa_problems)
validator_type (VARCHAR)
validation_status (VARCHAR) — PASSED | FAILED | WARNING
issues (TEXT[])
suggestions (TEXT[])
validated_at (TIMESTAMP)

INDEX: (problem_id, validation_status)
```

#### `dsa_generation_history`
```sql
id (UUID, PK)
generation_id (VARCHAR, unique)
user_id (UUID)
topic_id (UUID, FK)
difficulty (VARCHAR)
request_count (INT)
generated_count (INT)
generation_time_ms (INT)
status (VARCHAR)
created_at (TIMESTAMP)

INDEX: (user_id, created_at DESC), status
```

---

## 4. Architecture Patterns

### Caching Strategy
- **L1 Cache**: Redis distributed cache (5-30 min TTL)
- **L2 Cache**: Spring Cache local cache (1-5 min TTL)
- **Cache Keys**: `problem:template:{topicId}:{difficulty}`, `problem:instance:{problemId}`
- **Invalidation**: TTL-based + event-driven (when new problems added)
- **Warming**: Preload top 100 problems at startup

### Scalability
- **Horizontal**: Kubernetes (3-10 replicas, auto-scaling on CPU 80%)
- **Vertical**: Database read replicas, Redis cluster (3-6 nodes)
- **Concurrency**: Thread pool 20/core, queue 5000, timeout 30s

### Resilience
- **Circuit Breaker**: 5 consecutive failures → open for 30s
- **Retry Policy**: Exponential backoff (100ms → 800ms, 5 attempts)
- **Fallback**: Cache → database → error response
- **DLQ**: Failed operations queued to Kafka for retry

### Security
- **Authentication**: JWT bearer tokens
- **Authorization**: RBAC (ROLE_USER, ROLE_INSTRUCTOR, ROLE_ADMIN)
- **Rate Limiting**: 1000 req/min global, 100 req/min per-user, 500 req/min per-IP
- **Input Validation**: Topic UUID, difficulty enum, content sanitization
- **Encryption**: HTTPS/TLS 1.3, encrypted DB fields for sensitive data

---

## 5. Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Framework** | Spring Boot | 3.3.4 |
| **Java** | OpenJDK | 21 |
| **Build** | Maven | 3.9.x |
| **Database** | PostgreSQL | 15+ |
| **Cache** | Redis | 7.2+ |
| **ORM** | Spring Data JPA | 3.1.x |
| **API** | Spring Web | 3.3.4 |
| **Validation** | Spring Validation | 3.3.4 |
| **Serialization** | Jackson | 2.17.x |
| **Logging** | SLF4J + Logback | Latest |
| **Monitoring** | Micrometer + Prometheus | Latest |
| **Container** | Docker | Latest |
| **Orchestration** | Kubernetes | 1.28+ |
| **AI (Optional)** | OpenAI API / Azure OpenAI | Latest |

---

## 6. Deployment Topology

### Development
- Single Spring Boot instance
- H2 in-memory database
- Local Redis (optional)
- Port: 8080

### Staging
- 3-node Kubernetes cluster
- RDS PostgreSQL (replica)
- Redis standalone
- Monitoring + logging enabled

### Production
- Multi-AZ Kubernetes (3-10 pods)
- RDS PostgreSQL Multi-AZ
- Redis Cluster (3-6 nodes)
- CDN for static assets
- Auto-scaling policies
- Comprehensive monitoring/alerting

---

## 7. Failure Scenarios & Mitigations

| Scenario | Mitigation |
|----------|-----------|
| **Cache Unavailable** | Fall back to database (slower) |
| **DB Unavailable** | Return cached problems, queue new requests |
| **AI Engine Down** | Generate from templates only |
| **High Traffic Spike** | Rate limit, queue excess requests, auto-scale |
| **Network Partition** | Circuit breaker opens, return last good cached state |
| **Invalid Problem Generated** | Validation catches, logged to DLQ, retried |
| **Memory Pressure** | Evict oldest cache entries, scale pods |

---

## 8. Performance Targets

- **p99 Response Time**: < 200ms (cache hit), < 800ms (cache miss)
- **Throughput**: 5000 req/sec per pod (3-10 pods)
- **Cache Hit Rate**: 80%+ (after warmup)
- **Availability**: 99.9%
- **Problem Generation Time**: 50-200ms per problem
- **Database Query Time**: 10-50ms (indexed queries)

---

## 9. Monitoring & Observability

### Metrics
- Request count, latency, error rate (per endpoint)
- Cache hit/miss rate
- Database connection pool usage
- Thread pool queue size
- Generation time (p50, p95, p99)
- Problem validity rate

### Logging
- Structured JSON logs (ELK stack)
- Trace ID propagation (across services)
- Error context (stack traces, request payload)
- Audit logs (who generated what, when)

### Alerting
- **Critical**: Error rate > 5%, pod restart rate > 2/hour
- **High**: Response time p99 > 1000ms, cache hit rate < 70%
- **Medium**: DB connection pool usage > 80%, queue size > 1000

---

## 10. Phase 1 Implementation Scope

**In Scope**:
✅ REST API for problem generation  
✅ Spring Boot service + JPA repository  
✅ PostgreSQL schema with indexes  
✅ Redis caching layer  
✅ Problem validation framework  
✅ JUnit + Mockito tests  
✅ Docker containerization  
✅ Kubernetes deployment manifests  

**Out of Scope (Future Phases)**:
❌ AI/LLM integration (Phase 2)  
❌ Advanced analytics/reporting (Phase 3)  
❌ Problem recommendation engine (Phase 4)  
❌ Multi-tenant support (Phase 5)  

---

## 11. Acceptance Criteria

- [ ] REST API endpoints fully implemented and tested
- [ ] 80%+ unit test coverage
- [ ] 60%+ integration test coverage
- [ ] Performance: p99 < 200ms (cache), < 800ms (DB)
- [ ] All error scenarios handled (500/400/401/429)
- [ ] Kubernetes deployment working (dev + staging)
- [ ] Documentation complete (OpenAPI, README)
- [ ] Security review passed (OWASP Top 10)
- [ ] Code review approved
- [ ] Manual QA sign-off

---

## Decision Log

| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-04-24 | Spring Boot 3.3.4 | Standard for CodEval ecosystem |
| 2026-04-24 | PostgreSQL primary + Redis cache | Proven scalability, good for read-heavy workloads |
| 2026-04-24 | Kubernetes deployment | Enable horizontal scaling, align with cloud-native |
| 2026-04-24 | Async AI calls (optional) | Prevent blocking, enable better throughput |
| 2026-04-24 | Rate limiting + JWT | Standard API security practice |

---

**ARCHITECTURE APPROVED** ✅

Next Phase: **Backend Implementation** (Stage 2)

