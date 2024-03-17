package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.repository.BudgetDayRepository;
import com.aldegwin.budgetplanner.service.BudgetDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetDayServiceImpl implements BudgetDayService {
    private final BudgetDayRepository budgetDayRepository;

    @Override
    @Transactional
    public BudgetDay save(BudgetDay budgetDay) {
        if(budgetDay.getId() != null)
            throw new IdConflictException("Budget day ID must be null");
        return budgetDayRepository.save(budgetDay);
    }

    @Override
    @Transactional
    public BudgetDay update(BudgetDay budgetDay) {
        if(budgetDay.getId() == null)
            throw new IdConflictException("Budget day ID must be not null");
        return budgetDayRepository.save(budgetDay);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        budgetDayRepository.deleteById(id);
    }
}
