package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
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
    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public Budget findById(Long id) {
        Optional<Budget> optionalBudget = budgetRepository.findById(id);

        if(optionalBudget.isEmpty())
            throw new DatabaseEntityNotFoundException("Budget not found");

        return optionalBudget.get();
    }

    @Override
    public Iterable<Budget> findAll() {
        return budgetRepository.findAll();
    }

    @Override
    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
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
