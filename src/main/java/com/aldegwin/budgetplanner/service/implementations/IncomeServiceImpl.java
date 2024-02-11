package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.repository.IncomeRepository;
import com.aldegwin.budgetplanner.service.IncomeService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public void saveIncome(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public Optional<Income> findById(Long id) {
        return incomeRepository.findById(id);
    }

    @Override
    public Iterable<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public void update(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public void delete(Income income) {
        incomeRepository.delete(income);
    }

    @Override
    public void deleteById(Long id) {
        incomeRepository.deleteById(id);
    }
}
