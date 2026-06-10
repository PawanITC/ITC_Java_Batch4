package com.itc.linkedin.api_gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void handleExceptionShouldReturnErrorMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<String> response =
                handler.handleException(new RuntimeException("Test error"));

        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
        assertThat(response.getBody()).contains("Test error");
    }
}