package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Income;

import java.util.Optional;

public interface IncomeService {
    void saveIncome(Income income);

    Optional<Income> findById(Long id);

    Iterable<Income> findAll();

    void update(Income income);

    void delete(Income income);

    void deleteById(Long id);
}
