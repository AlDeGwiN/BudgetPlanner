package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Budget;

public interface BudgetService {
    Budget save(Budget budget);

    Budget findById(Long id);

    Iterable<Budget> findAll();

    Budget update(Budget budget);

    void delete(Budget budget);

    void deleteById(Long id);
}
