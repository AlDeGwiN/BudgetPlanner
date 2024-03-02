package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.repository.ExpenseRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public Expense save(Long user_id, Long budget_id, Expense expense) {
        Budget budget = budgetService.findById(user_id, budget_id);

        if(isExpenseDateNotValid(expense, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        budget.getExpenses().add(expense);
        expense.setBudget(budget);

        expenseRepository.save(expense);
        budgetService.update(user_id, budget);

        return expense;
    }

    @Override
    public Expense findById(Long user_id, Long budget_id, Long expense_id) {
        return budgetService.findById(user_id, budget_id).getExpenses().stream()
                .filter(e -> e.getId().equals(expense_id))
                .findFirst().orElseThrow(() -> new DatabaseEntityNotFoundException("Expense not found"));
    }

    @Override
    public Iterable<Expense> findAll(Long user_id, Long budget_id) {
        return budgetService.findById(user_id, budget_id).getExpenses();
    }

    @Override
    @Transactional
    public Expense update(Long user_id, Long budget_id, Expense expense) {
        Budget budget = budgetService.findById(user_id, budget_id);

        if(isExpenseDateNotValid(expense, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        budget.setExpenses(budget.getExpenses().stream()
                .map(i -> i.getId().equals(expense.getId()) ? expense : i)
                .collect(Collectors.toList()));

        expense.setBudget(budget);

        expenseRepository.save(expense);
        budgetService.update(user_id, budget);

        return expense;
    }

    @Override
    @Transactional
    public void deleteById(Long user_id, Long budget_id, Long expense_id) {
        Budget budget = budgetService.findById(user_id, budget_id);
        expenseRepository.deleteById(budget.getExpenses().stream()
                .filter(i -> i.getId().equals(expense_id))
                .findFirst()
                .orElseThrow(() -> new DatabaseEntityNotFoundException("Expense not found")).getId());
    }

    private boolean isExpenseDateNotValid(Expense expense, Budget budget) {
        return expense.getExpenseDate().isAfter(budget.getEndDate())
                || expense.getExpenseDate().isBefore(budget.getStartDate());
    }
}
