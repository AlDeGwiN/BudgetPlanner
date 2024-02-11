package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }


    public void save(Budget budget) {
        budgetRepository.save(budget);
    }

    public Optional<Budget> findById(Long id) {
        return budgetRepository.findById(id);
    }

    public Iterable<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public void update(Budget budget) {
        budgetRepository.save(budget);
    }

    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }

    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }
}
