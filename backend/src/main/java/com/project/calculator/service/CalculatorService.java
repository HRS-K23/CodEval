package com.project.calculator.service;

import com.project.calculator.dto.CalculationRequest;
import com.project.calculator.dto.CalculationResponse;

public interface CalculatorService {

    CalculationResponse add(CalculationRequest request);

    CalculationResponse subtract(CalculationRequest request);
}