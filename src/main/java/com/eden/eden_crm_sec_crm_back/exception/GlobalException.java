package com.eden.eden_crm_sec_crm_back.exception;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> errorResponse = ApiResponse.unprocessableEntity(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException ex) {
        ApiResponse<String> errorResponse = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ApiException>> handleRuntimeException(RuntimeException ex) {

        log.error("Runtime error: {}", ex.getMessage());

        ApiException apiException = new ApiException(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().toString()
        );

        ApiResponse<ApiException> errorResponse = ApiResponse.error(apiException, apiException.getHttpStatus());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(ValidationException ex) {

        // Create the payload as a map with the attribute and its message
        Map<String, String> payload = Map.of(ex.getAttribute(), ex.getMessage());

        // Build the ApiResponse with the payload
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.error(payload, HttpStatus.UNPROCESSABLE_ENTITY);

        // Return the ResponseEntity with the error response
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
