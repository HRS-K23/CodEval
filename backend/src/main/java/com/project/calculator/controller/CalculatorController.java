package com.project.calculator.controller;

import com.project.calculator.dto.CalculationRequest;
import com.project.calculator.dto.CalculationResponse;
import com.project.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/add")
    public ResponseEntity<CalculationResponse> add(@Valid @RequestBody CalculationRequest request) {
        return ResponseEntity.ok(calculatorService.add(request));
    }

    @PostMapping("/subtract")
    public ResponseEntity<CalculationResponse> subtract(@Valid @RequestBody CalculationRequest request) {
        return ResponseEntity.ok(calculatorService.subtract(request));
    }
}