package com.revpay.exception;

import com.revpay.dto.ApiError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getDefaultMessage()).orElse("Validation error");
        log.warn("Validation error: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", message);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad Request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Request", ex.getMessage());
    }

    /**
     * Handle IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Operation Not Allowed", ex.getMessage());
    }

    // Handle Missing Resources (404)
    // You can trigger this by throwing 'EntityNotFoundException' in your services
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(jakarta.persistence.EntityNotFoundException ex) {
        log.warn("Not Found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    // Catch-All for Internal Server Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        log.error("CRITICAL ERROR: ", ex);
        // Safety Upgrade: Don't leak raw exception messages to the user for 500 errors
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error",
                "An unexpected error occurred. Please contact support.");
    }

    // DRY Principle: Helper method to keep code clean
    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String errorType, String message) {
        ApiError error = new ApiError(status.value(), errorType, message);
        return ResponseEntity.status(status).body(error);
    }
}