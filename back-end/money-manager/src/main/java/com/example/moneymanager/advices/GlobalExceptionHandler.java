package com.example.moneymanager.advices;


import com.example.moneymanager.exceptions.BadRequestException;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exceptions) {
        ApiError apiError = ApiError.builder()
                .message(exceptions.getLocalizedMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException badRequestException) {
        ApiError apiError = ApiError.builder()
                .message(badRequestException.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAnyOtherException(Exception exceptions) {
        ApiError apiError = ApiError.builder()
                .message(exceptions.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException exception) {
        ApiError apiError = ApiError.builder()
                .message(exception.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();
        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(HttpServerErrorException.InternalServerError exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }


    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDatabaseExceptions(DataAccessException ex) {
        String message = "A database error occurred";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // 1. Handle Constraint Violations (Duplicate Entry, Foreign Key, etc.)
        if (ex instanceof DataIntegrityViolationException) {
            status = HttpStatus.CONFLICT; // 409
            message = (ex.getRootCause() != null) ? ex.getRootCause().getMessage() : ex.getMessage();
        }
        // 2. Handle Connection/Timeout issues
        else if (ex instanceof QueryTimeoutException ||
                ex instanceof DataAccessResourceFailureException) {
            status = HttpStatus.SERVICE_UNAVAILABLE; // 503
            message = "Database is currently unreachable. Please try again later.";
        }
        // 3. Handle Optimistic Locking (Concurrent updates)
        else if (ex instanceof OptimisticLockingFailureException) {
            status = HttpStatus.PRECONDITION_FAILED; // 412
            message = "The record was updated by another user. Please refresh and try again.";
        }
        // 4. Fallback for Syntax errors or general mapping issues
        else {
            message = (ex.getRootCause() != null) ? ex.getRootCause().getMessage() : ex.getMessage();
        }

        ApiError apiError = ApiError.builder()
                .message(message)
                .status(status)
                .build();

        return new ResponseEntity<>(new ApiResponse<>(apiError), status);
    }


////  To Handle Database Error
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
//        String message = "Database Constraint Violation";
//
//        // Extract the most specific cause (the actual SQL error message)
//        if (ex.getRootCause() != null) {
//            message = ex.getRootCause().getMessage();
//        } else {
//            message = ex.getMessage();
//        }
//
//        ApiError apiError = ApiError.builder()
//                .message(message)
//                .status(HttpStatus.CONFLICT) // 409 is better for duplicates, or use INTERNAL_SERVER_ERROR if preferred
//                .build();
//
//        return new ResponseEntity<>(new ApiResponse<>(apiError), HttpStatus.CONFLICT);
//    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

}
