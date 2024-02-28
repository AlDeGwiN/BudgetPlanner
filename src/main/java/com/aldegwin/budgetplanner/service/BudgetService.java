package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Budget;

public interface BudgetService {
    Budget save(Long user_id , Budget budget);

    Budget findById(Long user_id, Long budget_id);

    Iterable<Budget> findAllByUserId(Long id);

    Budget update(Long user_id, Budget updateableBudget);

    void deleteById(Long user_id, Long budget_id);
}
