package com.project.calculator.service.impl;

import com.project.calculator.dto.CalculationRequest;
import com.project.calculator.dto.CalculationResponse;
import com.project.calculator.service.CalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public CalculationResponse add(CalculationRequest request) {
        BigDecimal result = request.a().add(request.b());
        return new CalculationResponse("add", request.a(), request.b(), result);
    }

    @Override
    public CalculationResponse subtract(CalculationRequest request) {
        BigDecimal result = request.a().subtract(request.b());
        return new CalculationResponse("subtract", request.a(), request.b(), result);
    }
}