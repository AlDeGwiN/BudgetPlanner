package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Income;

public interface IncomeService {
    Income save(Long user_id, Long budget_id, Income income);

    Income findById(Long user_id, Long budget_id, Long income_id);

    Iterable<Income> findAll(Long user_id, Long budget_id);

    Income update(Long user_id, Long budget_id, Income income);

    void deleteById(Long user_id, Long budget_id, Long income_id);
}
