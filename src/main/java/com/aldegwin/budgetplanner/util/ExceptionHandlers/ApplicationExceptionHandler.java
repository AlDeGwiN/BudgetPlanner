package com.aldegwin.budgetplanner.util.ExceptionHandlers;

import com.aldegwin.budgetplanner.communication.response.error.Error;
import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.communication.response.error.ErrorResponse;
import com.aldegwin.budgetplanner.communication.response.error.ErrorsResponse;
import com.aldegwin.budgetplanner.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Service
@ControllerAdvice
public class ApplicationExceptionHandler {
    private final HttpStatus INTERNAL_SERVER_ERROR_STATUS;
    private final HttpStatus BAD_REQUEST_STATUS;
    private final HttpStatus NOT_FOUND_STATUS;
    private final MediaType DEFAULT_CONTENT_TYPE;

    public ApplicationExceptionHandler() {
        INTERNAL_SERVER_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
        BAD_REQUEST_STATUS = HttpStatus.BAD_REQUEST;
        NOT_FOUND_STATUS = HttpStatus.NOT_FOUND;
        DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(IdConflictException.class)
    public ResponseEntity<ErrorResponse> handleBudgetPlannerException(IdConflictException exception) {
        return ResponseEntity.status(BAD_REQUEST_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(createErrorResponse(ErrorCode.VALIDATION_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        return ResponseEntity.status(BAD_REQUEST_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(createErrorResponse(ErrorCode.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(DatabaseEntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseEntityNotFoundException(
            DatabaseEntityNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(createErrorResponse(ErrorCode.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(IncorrectDateException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectIncomeDateException(IncorrectDateException exception) {
        return ResponseEntity.status(BAD_REQUEST_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(createErrorResponse(ErrorCode.VALIDATION_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(NotUniqueFieldException.class)
    public ResponseEntity<ErrorsResponse> handleNotUniqueFieldException(
            NotUniqueFieldException e) {
        return ResponseEntity.status(BAD_REQUEST_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(ErrorsResponse.builder()
                        .errors(e.getErrors()
                                .entrySet()
                                .stream()
                                .map(entry ->
                                        Error.builder()
                                        .errorCode(entry.getKey())
                                        .message(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        return ResponseEntity.status(BAD_REQUEST_STATUS)
                .contentType(DEFAULT_CONTENT_TYPE)
                .body(ErrorsResponse.builder()
                        .errors(methodArgumentNotValidException
                                .getFieldErrors().stream()
                                .map(e -> Error.builder()
                                        .errorCode(ErrorCode.VALIDATION_ERROR)
                                        .message(e.getDefaultMessage()).build())
                                .collect(Collectors.toList()))
                        .build());
    }

    private ErrorResponse createErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .error(Error.builder()
                        .errorCode(errorCode)
                        .message(message)
                        .build())
                .build();
    }
}
