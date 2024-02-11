package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public void saveIncome(Income income) {
        incomeRepository.save(income);
    }

    public Optional<Income> findById(Long id) {
        return incomeRepository.findById(id);
    }

    public Iterable<Income> findAll() {
        return incomeRepository.findAll();
    }

    public void update(Income income) {
        incomeRepository.save(income);
    }

    public void delete(Income income) {
        incomeRepository.delete(income);
    }

    public void deleteById(Long id) {
        incomeRepository.deleteById(id);
    }
}
