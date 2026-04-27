package com.project.calculator.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private Validator validator;

    /**
     * Test handleUnreadableBody() with malformed JSON (invalid JSON syntax)
     * Triggers HttpMessageNotReadableException
     */
    @Test
    void handleUnreadableBody_malformedJson_returnsBadRequest() throws Exception {
        String malformedJson = "{\"a\": 10, \"b\": 5,}"; // trailing comma makes it invalid

        mockMvc.perform(post("/api/v1/calculator/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body is malformed"))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    /**
     * Test handleUnreadableBody() with invalid JSON characters
     * Triggers HttpMessageNotReadableException
     */
    @Test
    void handleUnreadableBody_invalidJsonCharacters_returnsBadRequest() throws Exception {
        String invalidJson = "{invalid json}"; // completely invalid JSON

        mockMvc.perform(post("/api/v1/calculator/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body is malformed"));
    }

    /**
     * Test handleUnreadableBody() with unclosed JSON object
     * Triggers HttpMessageNotReadableException
     */
    @Test
    void handleUnreadableBody_unclosedJsonObject_returnsBadRequest() throws Exception {
        String unclosedJson = "{\"a\": 10, \"b\": 5"; // missing closing brace

        mockMvc.perform(post("/api/v1/calculator/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(unclosedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is malformed"));
    }

    /**
     * Test handleConstraintViolation() by directly invoking the exception handler
     * with a ConstraintViolationException
     */
    @Test
    void handleConstraintViolation_invalidConstraintViolations_returnsBadRequest() {
        // Create a mock ConstraintViolation
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violations found",
                violations
        );

        org.springframework.mock.web.MockHttpServletRequest mockRequest = 
                new org.springframework.mock.web.MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/calculator/add");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleConstraintViolation(exception, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Request is invalid");
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/calculator/add");
    }

    /**
     * Test handleUnexpected() by directly invoking the exception handler
     * with a generic Exception
     */
    @Test
    void handleUnexpected_genericException_returnsInternalServerError() {
        Exception exception = new RuntimeException("Unexpected runtime error");

        org.springframework.mock.web.MockHttpServletRequest mockRequest = 
                new org.springframework.mock.web.MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/calculator/add");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleUnexpected(exception, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected internal error");
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().path()).isEqualTo("/api/v1/calculator/add");
    }

    /**
     * Test handleUnexpected() with a NullPointerException
     */
    @Test
    void handleUnexpected_nullPointerException_returnsInternalServerError() {
        Exception exception = new NullPointerException("Null pointer encountered");

        org.springframework.mock.web.MockHttpServletRequest mockRequest = 
                new org.springframework.mock.web.MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/calculator/subtract");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleUnexpected(exception, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected internal error");
    }

    /**
     * Test handleUnexpected() with an IllegalArgumentException
     */
    @Test
    void handleUnexpected_illegalArgumentException_returnsInternalServerError() {
        Exception exception = new IllegalArgumentException("Invalid argument provided");

        org.springframework.mock.web.MockHttpServletRequest mockRequest = 
                new org.springframework.mock.web.MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/calculator/add");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleUnexpected(exception, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected internal error");
    }
}
