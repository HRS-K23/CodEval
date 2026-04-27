# Unit Test Execution Report

**Generated:** 2026-04-24 11:02:07
**Project:** calculator-service
**Maven Command:** `mvn clean test jacoco:report "-Dmaven.test.failure.ignore=true"`

---

## Summary

| Metric                                  | Value                                          |
|-----------------------------------------|------------------------------------------------|
| Run Mode                                | INCREMENTAL                                    |
| Files Changed (Incremental)             | 0 — test generation skipped                    |
| Total Classes Scanned                   | 7                                              |
| Total Test Classes Generated            | N/A                                            |
| Total Test Classes Updated              | 0                                              |
| Total Test Methods Written              | N/A                                            |
| Tests Passed                            | 5                                              |
| Tests Failed                            | 0                                              |
| Tests Skipped / Disabled                | 0                                              |
| Build Status                            | PASSED                                         |

---

## Coverage Summary (JaCoCo)

| Metric               | Actual          | Target | Status        |
|----------------------|-----------------|--------|---------------|
| Line Coverage        | 75.8%           | 80%    | ❌             |
| Branch Coverage      | 73.7%           | 70%    | ✅             |
| Instruction Coverage | 77.0%           | 75%    | ✅             |

---

## Class-wise Coverage

| Class Name                    | Package                              | Line Coverage | Branch Coverage | Status        |
|-------------------------------|--------------------------------------|---------------|-----------------|---------------|
| CalculatorController          | com.project.calculator.controller    | 100.0%        | 100.0%          | ✅             |
| CalculatorServiceImpl          | com.project.calculator.service.impl  | 100.0%        | 100.0%          | ✅             |
| ApiErrorResponse              | com.project.calculator.exception     | 100.0%        | N/A             | ✅             |
| CalculationRequest            | com.project.calculator.dto           | 100.0%        | N/A             | ✅             |
| CalculationResponse           | com.project.calculator.dto           | 100.0%        | N/A             | ✅             |
| GlobalExceptionHandler        | com.project.calculator.exception     | 64.7%         | 56.0%           | ❌             |
| CalculatorServiceApplication  | com.project.calculator               | 50.0%         | 50.0%           | ❌             |

---

## Failed Tests

No test failures recorded.

---

## Coverage Gaps

| Class                         | Method                                            | Line Coverage | Branch Coverage | Reason                                                               |
|-------------------------------|---------------------------------------------------|---------------|-----------------|----------------------------------------------------------------------|
| GlobalExceptionHandler        | handleUnreadableBody()                            | 0.0%          | 0.0%            | NO_TEST_METHOD                                                       |
| GlobalExceptionHandler        | handleConstraintViolation()                       | 0.0%          | 0.0%            | NO_TEST_METHOD                                                       |
| GlobalExceptionHandler        | handleUnexpected()                                | 0.0%          | 0.0%            | NO_TEST_METHOD                                                       |
| CalculatorServiceApplication  | main(String[] args)                               | 0.0%          | 0.0%            | NO_TEST_METHOD                                                       |

---

## pom.xml Changes Made

- Added plugin: `org.jacoco:jacoco-maven-plugin:0.8.11`

---

## Notes

- Tests scoped to calculator service; no changes to source files detected
- Test configuration file created: `src/test/resources/application.properties`
- No production code was modified
- Existing test classes: CalculatorServiceApplicationTests, CalculatorControllerIntegrationTest
- Line coverage (75.8%) is 4.2% below target (80%)
- Coverage gaps identified in GlobalExceptionHandler exception handling methods and application bootstrap main method
