package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.repository.IncomeRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public Income save(Long user_id, Long budget_id, Income income) {
        Budget budget = budgetService.findById(user_id, budget_id);

        if(isIncomeDateNotValid(income, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        budget.getIncomes().add(income);
        income.setBudget(budget);

        incomeRepository.save(income);
        budgetService.update(user_id, budget);

        return income;
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
        Budget budget = budgetService.findById(user_id, budget_id);

        if (isIncomeDateNotValid(income, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        budget.setIncomes(budget.getIncomes().stream()
                .map(i -> i.getId().equals(income.getId()) ? income : i)
                .collect(Collectors.toList()));

        income.setBudget(budget);

        incomeRepository.save(income);
        budgetService.update(user_id, budget);

        return income;
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

    private boolean isIncomeDateNotValid(Income income, Budget budget) {
        return income.getIncomeDate().isAfter(budget.getEndDate())
                || income.getIncomeDate().isBefore(budget.getStartDate());
    }
}
