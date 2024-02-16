package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.repository.ExpenseRepository;
import com.aldegwin.budgetplanner.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public Iterable<Expense> findAll() {
        return expenseRepository.findAll();
    }

    @Override
    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void delete(Expense expense) {
        expenseRepository.delete(expense);
    }

    @Override
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }
}
