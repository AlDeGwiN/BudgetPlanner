package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.IncomeType;

public interface IncomeTypeService {
    IncomeType findById(Long id);

    Iterable<IncomeType> findAll();
}
