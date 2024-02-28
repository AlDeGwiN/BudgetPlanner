package com.aldegwin.budgetplanner.util;

import com.aldegwin.budgetplanner.communication.response.error.Error;
import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.communication.response.error.ErrorResponse;
import com.aldegwin.budgetplanner.communication.response.error.ValidErrorResponse;
import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.NotUniqueFieldException;
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
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                                .message(runtimeException.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(IdConflictException.class)
    public ResponseEntity<ErrorResponse> handleBudgetPlannerException (
            IdConflictException idConflictException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.VALIDATION_ERROR)
                                .message(idConflictException.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException noResourceFoundException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.NOT_FOUND)
                                .message(noResourceFoundException.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(DatabaseEntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseEntityNotFoundException(
            DatabaseEntityNotFoundException dataBaseEntityNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.NOT_FOUND)
                                .message(dataBaseEntityNotFoundException.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(NotUniqueFieldException.class)
    public ResponseEntity<ValidErrorResponse> handleNotUniqueFieldException(
            NotUniqueFieldException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ValidErrorResponse.builder()
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
    public ResponseEntity<ValidErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ValidErrorResponse.builder()
                        .errors(methodArgumentNotValidException
                                .getFieldErrors().stream()
                                .map(e -> Error.builder()
                                        .errorCode(ErrorCode.VALIDATION_ERROR)
                                        .message(e.getDefaultMessage()).build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
