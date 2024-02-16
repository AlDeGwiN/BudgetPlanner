package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.BudgetDay;

import java.util.Optional;

public interface BudgetDayService {
    BudgetDay save(BudgetDay budgetDay);

    Optional<BudgetDay> findById(Long id);

    Iterable<BudgetDay> findAll();

    BudgetDay update(BudgetDay budgetDay);

    void delete(BudgetDay budgetDay);

    void deleteById(Long id);
}
