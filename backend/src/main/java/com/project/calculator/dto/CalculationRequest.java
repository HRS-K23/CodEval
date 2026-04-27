package com.project.calculator.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculationRequest(
        @NotNull(message = "Field 'a' is required") BigDecimal a,
        @NotNull(message = "Field 'b' is required") BigDecimal b) {
}