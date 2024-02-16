package com.aldegwin.budgetplanner.communication.response.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Error {
    private ErrorCode errorCode;
    private String message;
}
