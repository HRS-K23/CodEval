# Implementation Artifacts — EPMICMPCOD-258

**Stage:** 2 — Backend Implementation
**Generated:** 2026-04-27T12:52:00Z
**Agent:** Backend Implementation Design Agent

---

## Files Created

### Root
| File | Description |
|------|-------------|
| `pom.xml` | Maven build — Spring Boot 3.2.5, JPA, H2, Caffeine, Cache |

### Main Application
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/DsaGeneratorApplication.java` | Spring Boot entry point (`@EnableCaching`, `@EnableAsync`) |

### Model
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/model/Difficulty.java` | Enum: EASY, MEDIUM, HARD |
| `src/main/java/com/codeval/dsagenerator/model/Category.java` | Enum: ARRAYS, TREES, GRAPHS, DP, STRINGS, SORTING |
| `src/main/java/com/codeval/dsagenerator/model/ProblemTemplate.java` | JPA entity — problem template |
| `src/main/java/com/codeval/dsagenerator/model/ProblemParameterSet.java` | JPA entity — template parameter ranges |
| `src/main/java/com/codeval/dsagenerator/model/GenerationLog.java` | JPA entity — audit generation log |

### Repository
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/repository/ProblemTemplateRepository.java` | Spring Data JPA — find by difficulty / category |
| `src/main/java/com/codeval/dsagenerator/repository/GenerationLogRepository.java` | Spring Data JPA — find seen template IDs by sessionId |

### Service
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/service/ProblemGenerationService.java` | Interface |
| `src/main/java/com/codeval/dsagenerator/service/DifficultyCalibrator.java` | Normalises + validates difficulty input |
| `src/main/java/com/codeval/dsagenerator/service/DeduplicationFilter.java` | Filters already-seen templates per sessionId |
| `src/main/java/com/codeval/dsagenerator/service/AuditLogService.java` | `@Async` audit log writer |
| `src/main/java/com/codeval/dsagenerator/service/impl/ProblemGenerationServiceImpl.java` | Core generation logic with Caffeine caching |

### Controller
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/controller/ProblemController.java` | `GET /api/v1/problems/generate` REST endpoint |

### Exception
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/exception/NoProblemsAvailableException.java` | 404 domain exception |
| `src/main/java/com/codeval/dsagenerator/exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` — handles 400, 404, 500 |

### Config
| File | Description |
|------|-------------|
| `src/main/java/com/codeval/dsagenerator/config/CacheConfig.java` | Caffeine cache manager (200 entries, 10 min TTL) |
| `src/main/java/com/codeval/dsagenerator/config/AsyncConfig.java` | Async thread pool for audit executor |

### Resources
| File | Description |
|------|-------------|
| `src/main/resources/application.properties` | H2 datasource, JPA, H2 console config |
| `src/main/resources/data.sql` | 15 seed problem templates across all difficulties/categories |

---

## Total Files: 20
