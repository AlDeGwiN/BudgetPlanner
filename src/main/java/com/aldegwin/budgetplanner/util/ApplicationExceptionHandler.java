package com.aldegwin.budgetplanner.util;

import com.aldegwin.budgetplanner.communication.response.Response;
import com.aldegwin.budgetplanner.communication.response.error.Error;
import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.communication.response.error.ErrorResponse;
import com.aldegwin.budgetplanner.communication.response.error.ValidErrorResponse;
import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
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
    public ResponseEntity<Response> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                                .message(e.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response> handleNoResourceFoundException(
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
    public ResponseEntity<Response> handleUserNotFoundException(
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationException(
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
