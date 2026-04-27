# Implementation Notes — EPMICMPCOD-258

**Stage:** 2 — Backend Implementation
**Generated:** 2026-04-27T12:52:00Z

---

## Architecture Pattern

Controller → Service (interface) → ServiceImpl → Repository → Entity

All business logic is in `ProblemGenerationServiceImpl`. Controllers contain no business logic.

---

## Key Implementation Decisions

| Decision | Detail |
|---|---|
| `@Cacheable` on `fetchCandidates` | Template queries are read-heavy; Caffeine caches per `difficulty_category` key for 10 min |
| `@Async` on `AuditLogService.log()` | Audit writes are off the request thread — no latency impact |
| Deduplication fallback | If all templates for a session have been seen, the filter resets (returns full list) to avoid 404 |
| `sessionId` max 255 chars | Enforced in controller before reaching service layer |
| `data.sql` seed data | 15 templates seeded at startup — covers all 6 categories × EASY/MEDIUM difficulties |
| H2 file-backed (not in-memory) | `jdbc:h2:file:./data/dsagenerator` — data persists across restarts in dev |

---

## How to Run

```bash
mvn clean package
java -jar target/dsa-generator-1.0.0-SNAPSHOT.jar
```

Or in dev:
```bash
mvn spring-boot:run
```

### Sample Request
```
GET http://localhost:8080/api/v1/problems/generate?difficulty=MEDIUM&category=ARRAYS&sessionId=user123
```

### H2 Console (dev)
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/dsagenerator
Username: sa
Password: (blank)
```

---

## Known Limitations (MVP)

- `sessionId` is client-supplied and unauthenticated — not a security concern for MVP (no PII linked)
- No rate limiting in-app — expected at reverse proxy/API gateway level
- `ProblemParameterSet` table is created but not yet wired into the parameterisation logic (future enhancement)
- Parameter substitution (`{n}`, `{max}`, `{min}`) is a simple regex replace — a proper template engine is a future enhancement
