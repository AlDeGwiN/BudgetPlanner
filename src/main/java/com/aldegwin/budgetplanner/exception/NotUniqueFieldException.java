package com.aldegwin.budgetplanner.exception;

import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class NotUniqueFieldException extends BudgetPlannerException{
    Map<ErrorCode, String> errors;

    public NotUniqueFieldException(String message, Map<ErrorCode, String> errors) {
        super(message);
        this.errors = errors;
    }
}
