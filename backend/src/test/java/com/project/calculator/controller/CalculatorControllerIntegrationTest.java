package com.project.calculator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CalculatorControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void addShouldReturnCalculatedSum() {
        ResponseEntity<Map> response = postJson("/api/v1/calculator/add", Map.of("a", 10, "b", 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("operation", "add");
        assertThat(response.getBody()).containsEntry("result", 15);
    }

    @Test
    void subtractShouldReturnCalculatedDifference() {
        ResponseEntity<Map> response = postJson("/api/v1/calculator/subtract", Map.of("a", 10, "b", 5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("operation", "subtract");
        assertThat(response.getBody()).containsEntry("result", 5);
    }

    @Test
    void invalidRequestShouldReturnBadRequest() {
        ResponseEntity<Map> response = postJson("/api/v1/calculator/add", Map.of("a", 10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Request body is invalid");
    }

    @Test
    void healthEndpointShouldReportUp() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    private ResponseEntity<Map> postJson(String path, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(
                baseUrl() + path,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}