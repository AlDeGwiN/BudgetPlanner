package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void save(Expense expense) {
        expenseRepository.save(expense);
    }

    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    public Iterable<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public void update(Expense expense) {
        expenseRepository.save(expense);
    }

    public void delete(Expense expense) {
        expenseRepository.delete(expense);
    }

    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }
}
