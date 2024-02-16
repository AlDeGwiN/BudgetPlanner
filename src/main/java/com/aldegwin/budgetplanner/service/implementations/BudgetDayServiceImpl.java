package com.aldegwin.budgetplanner.service.implementations;

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
    public Optional<BudgetDay> findById(Long id) {
        return budgetDayRepository.findById(id);
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
