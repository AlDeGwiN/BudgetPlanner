package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.IncomeType;

import java.util.Optional;

public interface IncomeTypeService {
    Optional<IncomeType> findById(Long id);

    Iterable<IncomeType> findAll();
}
