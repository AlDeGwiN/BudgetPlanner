package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.repository.ExpenseRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public Expense save(Long user_id, Long budget_id, Expense expense) {
        if(expense.getId() != null)
            throw new IdConflictException("Expense ID must be null");

        Budget budget = budgetService.findById(user_id, budget_id);

        if(isExpenseDateNotValid(expense, budget))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        expense.setBudget(budget);

        return expenseRepository.save(expense);
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
        if(expense.getId() == null)
            throw new IdConflictException("Expense ID must be not null");

        Expense existingExpense = findById(user_id, budget_id ,expense.getId());

        if(isExpenseDateNotValid(expense, existingExpense.getBudget()))
            throw new IncorrectDateException("The entered date is not included in the budget deadline");

        existingExpense.setExpenseDate(expense.getExpenseDate());
        existingExpense.setExpenseType(expense.getExpenseType());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDescription(expense.getDescription());

        return expenseRepository.save(existingExpense);
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

    @Override
    @Transactional
    public void deleteUnnecessaryExpensesForBudget(Budget budget) {
        List<Expense> oldExpenses = budget.getExpenses();
        List<Expense> newExpenses = new ArrayList<>();
        for(Expense expense : oldExpenses) {
            if(isExpenseDateNotValid(expense, budget))
                deleteById(budget.getUser().getId(), budget.getId(), expense.getId());
            else
                newExpenses.add(expense);
        }
        budget.setExpenses(newExpenses);
    }

    private boolean isExpenseDateNotValid(Expense expense, Budget budget) {
        return expense.getExpenseDate().isAfter(budget.getEndDate())
                || expense.getExpenseDate().isBefore(budget.getStartDate());
    }
}
