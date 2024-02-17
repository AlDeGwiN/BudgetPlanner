package com.aldegwin.budgetplanner.util;

import com.aldegwin.budgetplanner.communication.response.Response;
import com.aldegwin.budgetplanner.communication.response.error.Error;
import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.communication.response.error.ErrorResponse;
import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@ControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handeRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                                .message(e.getMessage())
                                .build())
                        .build());
    }

    @ExceptionHandler(DatabaseEntityNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFoundException(
            DatabaseEntityNotFoundException dataBaseEntityNotFoundException) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .error(Error.builder()
                                .errorCode(ErrorCode.NOT_FOUND)
                                .message(dataBaseEntityNotFoundException.getMessage())
                                .build())
                        .build());
    }
}
