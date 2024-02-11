package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.ExpenseType;
import com.aldegwin.budgetplanner.repository.ExpenseTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseTypeService {
    private final ExpenseTypeRepository expenseTypeRepository;

    public ExpenseTypeService(ExpenseTypeRepository expenseTypeRepository) {
        this.expenseTypeRepository = expenseTypeRepository;
    }

    public Optional<ExpenseType> findById(Long id) {
        return expenseTypeRepository.findById(id);
    }

    public Iterable<ExpenseType> findAll() {
        return expenseTypeRepository.findAll();
    }
}
