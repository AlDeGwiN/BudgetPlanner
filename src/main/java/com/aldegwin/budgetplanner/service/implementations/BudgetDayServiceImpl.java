package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.repository.BudgetDayRepository;
import com.aldegwin.budgetplanner.service.BudgetDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetDayServiceImpl implements BudgetDayService {
    private final BudgetDayRepository budgetDayRepository;
    @Override
    public BudgetDay save(BudgetDay budgetDay) {
        return budgetDayRepository.save(budgetDay);
    }

    @Override
    public BudgetDay findById(Long id) {
        Optional<BudgetDay> budgetDayOptional = budgetDayRepository.findById(id);

        if(budgetDayOptional.isEmpty())
            throw new DatabaseEntityNotFoundException("Budget day not found");

        return budgetDayOptional.get();
    }

    @Override
    public Iterable<BudgetDay> findAll() {
        return budgetDayRepository.findAll();
    }

    @Override
    public BudgetDay update(BudgetDay budgetDay) {
        return budgetDayRepository.save(budgetDay);
    }

    @Override
    public void delete(BudgetDay budgetDay) {
        budgetDayRepository.delete(budgetDay);
    }

    @Override
    public void deleteById(Long id) {
        budgetDayRepository.deleteById(id);
    }
}
