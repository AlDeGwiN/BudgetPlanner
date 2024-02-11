package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }


    @Override
    public void save(Budget budget) {
        budgetRepository.save(budget);
    }

    @Override
    public Optional<Budget> findById(Long id) {
        return budgetRepository.findById(id);
    }

    @Override
    public Iterable<Budget> findAll() {
        return budgetRepository.findAll();
    }

    @Override
    public void update(Budget budget) {
        budgetRepository.save(budget);
    }

    @Override
    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }

    @Override
    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }
}
