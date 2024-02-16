package com.aldegwin.budgetplanner.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
public class BudgetPlannerException extends RuntimeException{
    private String message;
}
