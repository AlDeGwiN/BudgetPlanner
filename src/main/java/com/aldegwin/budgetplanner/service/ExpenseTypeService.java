package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.ExpenseType;

public interface ExpenseTypeService {
    ExpenseType findById(Long id);

    Iterable<ExpenseType> findAll();
}
