package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Budget;

import java.util.Optional;

public interface BudgetService {
    void save(Budget budget);

    Optional<Budget> findById(Long id);

    Iterable<Budget> findAll();

    void update(Budget budget);

    void delete(Budget budget);

    void deleteById(Long id);
}
