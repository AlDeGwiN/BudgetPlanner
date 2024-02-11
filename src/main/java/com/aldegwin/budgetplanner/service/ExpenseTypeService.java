package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.ExpenseType;

import java.util.Optional;

public interface ExpenseTypeService {
    Optional<ExpenseType> findById(Long id);

    Iterable<ExpenseType> findAll();
}
