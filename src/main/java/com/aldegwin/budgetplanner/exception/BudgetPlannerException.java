package com.aldegwin.budgetplanner.exception;

import lombok.Data;

@Data
public class BudgetPlannerException extends RuntimeException{
    private final String message;
}
