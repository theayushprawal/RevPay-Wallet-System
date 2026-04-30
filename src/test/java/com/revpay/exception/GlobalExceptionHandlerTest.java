package com.revpay.exception;

import com.revpay.dto.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // 1️⃣ Validation Exception Test
    @Test
    void testHandleValidationException() {

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "object",
                "field",
                "Field cannot be empty"
        );

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiError> response =
                handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals("Field cannot be empty", response.getBody().getMessage());
    }

    // 2️⃣ IllegalArgumentException Test
    @Test
    void testHandleIllegalArgumentException() {

        IllegalArgumentException ex =
                new IllegalArgumentException("Invalid input");

        ResponseEntity<ApiError> response =
                handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Request", response.getBody().getError());
        assertEquals("Invalid input", response.getBody().getMessage());
    }

    // 3️⃣ IllegalStateException Test
    @Test
    void testHandleIllegalStateException() {

        IllegalStateException ex =
                new IllegalStateException("Operation not allowed");

        ResponseEntity<ApiError> response =
                handler.handleIllegalState(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Operation Not Allowed", response.getBody().getError());
        assertEquals("Operation not allowed", response.getBody().getMessage());
    }

    // 4️⃣ General Exception Test
    @Test
    void testHandleGeneralException() {

        Exception ex = new Exception("Server failure");

        ResponseEntity<ApiError> response =
                handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please contact support.", response.getBody().getMessage());
    }
}
