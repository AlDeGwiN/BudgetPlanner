package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Expense;

public interface ExpenseService {
    Expense save(Expense expense);

    Expense findById(Long id);

    Iterable<Expense> findAll();

    Expense update(Expense expense);

    void delete(Expense expense);

    void deleteById(Long id);
}
