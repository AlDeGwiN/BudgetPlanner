package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.BudgetDay;

public interface BudgetDayService {
    BudgetDay save(BudgetDay budgetDay);
    BudgetDay update(BudgetDay budgetDay);
    void deleteById(Long id);
}
