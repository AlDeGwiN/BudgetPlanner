package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.BudgetDay;

import java.util.List;

public interface BudgetCalculatingService {
    List<BudgetDay> createBudgetDays(Budget budget);
    void calculateBudget(Budget budget);
    void reformatBudgetDays(Budget budget);
}
