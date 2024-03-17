package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.repository.IncomeRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public Income save(Long user_id, Long budget_id, Income income) {
        if(income.getId() != null)
            throw new IdConflictException("Income ID must be null");

        Budget budget = budgetService.findById(user_id, budget_id);

        if(isIncomeDateNotValid(income, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        income.setBudget(budget);

        return incomeRepository.save(income);
    }

    @Override
    public Income findById(Long user_id, Long budget_id, Long income_id) {
        return budgetService.findById(user_id, budget_id).getIncomes().stream()
                .filter(i -> i.getId().equals(income_id))
                .findFirst().orElseThrow(() -> new DatabaseEntityNotFoundException("Income not found"));
    }

    @Override
    public Iterable<Income> findAll(Long user_id, Long budget_id) {
        return budgetService.findById(user_id, budget_id).getIncomes();
    }

    @Override
    @Transactional
    public Income update(Long user_id, Long budget_id, Income income) {
        if(income.getId() == null)
            throw new IdConflictException("Income ID must be not null");

        Income existingIncome = findById(user_id, budget_id, income.getId());

        if (isIncomeDateNotValid(income, existingIncome.getBudget()))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        existingIncome.setIncomeDate(income.getIncomeDate());
        existingIncome.setIncomeType(income.getIncomeType());
        existingIncome.setAmount(income.getAmount());
        existingIncome.setDescription(income.getDescription());

        return incomeRepository.save(existingIncome);
    }

    @Override
    @Transactional
    public void deleteById(Long user_id, Long budget_id, Long income_id) {
        Budget budget = budgetService.findById(user_id, budget_id);
        incomeRepository.deleteById(budget.getIncomes().stream()
                .filter(i -> i.getId().equals(income_id))
                .findFirst()
                .orElseThrow(() -> new DatabaseEntityNotFoundException("Income not found")).getId());
    }

    @Override
    @Transactional
    public void deleteUnnecessaryIncomesForBudget(Budget budget) {
        List<Income> oldIncomes = budget.getIncomes();
        List<Income> newIncomes = new ArrayList<>();
        for(Income income : oldIncomes) {
            if(isIncomeDateNotValid(income, budget))
                deleteById(budget.getUser().getId(), budget.getId(), income.getId());
            else
                newIncomes.add(income);
        }
        budget.setIncomes(newIncomes);
    }

    private boolean isIncomeDateNotValid(Income income, Budget budget) {
        return income.getIncomeDate().isAfter(budget.getEndDate())
                || income.getIncomeDate().isBefore(budget.getStartDate());
    }
}
