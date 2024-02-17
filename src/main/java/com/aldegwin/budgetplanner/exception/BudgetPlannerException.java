package com.aldegwin.budgetplanner.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetPlannerException extends RuntimeException{
    private final String message;
}
