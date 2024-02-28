package com.aldegwin.budgetplanner.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BudgetPlannerException extends RuntimeException{
    public BudgetPlannerException(String message) {
        super(message);
    }
}
