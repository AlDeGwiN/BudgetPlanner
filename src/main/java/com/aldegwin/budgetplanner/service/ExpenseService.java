package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Expense;

public interface ExpenseService {
    Expense save(Long user_id, Long budget_id, Expense expense);

    Expense findById(Long user_id, Long budget_id, Long expense_id);

    Iterable<Expense> findAll(Long user_id, Long budget_id);

    Expense update(Long user_id, Long budget_id, Expense expense);

    void deleteById(Long user_id, Long budget_id, Long expense_id);
}
