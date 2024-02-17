package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
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
    public Income saveIncome(Income income) {
        return incomeRepository.save(income);
    }

    @Override
    public Income findById(Long id) {
        Optional<Income> optionalIncome = incomeRepository.findById(id);

        if(optionalIncome.isEmpty())
            throw new DatabaseEntityNotFoundException("Income not found");

        return optionalIncome.get();
    }

    @Override
    public Iterable<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public Income update(Income income) {
        return incomeRepository.save(income);
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
