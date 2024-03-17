package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import com.aldegwin.budgetplanner.service.BudgetCalculatingService;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetCalculatingService budgetCalculatingService;
    private final UserService userService;

    @Override
    @Transactional
    public Budget save(Long user_id, Budget budget) {
        if (budget.getId() != null)
            throw new IdConflictException("Budget ID must be null");

        if(isBudgetDatesNotValid(budget))
            throw new IncorrectDateException("Incorrect budget period");

        User user = userService.findById(user_id);

        budget.setUser(user);

        Budget savedBudget = budgetRepository.save(budget);
        List<BudgetDay> budgetDays = budgetCalculatingService.createBudgetDays(budget);
        budget.setBudgetDays(budgetDays);

        return savedBudget;
    }

    @Override
    public Budget findById(Long user_id, Long budget_id) {
        User user = userService.findById(user_id);
        return user.getBudgets().stream()
                .filter(b -> b.getId().equals(budget_id))
                .findFirst()
                .orElseThrow(() -> new DatabaseEntityNotFoundException("Budget not found"));
    }

    @Override
    public Iterable<Budget> findAll(Long user_id) {
        User user = userService.findById(user_id);
        return user.getBudgets();
    }

    @Override
    @Transactional
    public Budget update(Long user_id, Budget budget) {
        if(budget.getId() == null)
            throw new IdConflictException("Budget ID must be not null");

        if(isBudgetDatesNotValid(budget))
            throw new IncorrectDateException("Incorrect budget period");

        Budget existingBudget = findById(user_id, budget.getId());

        LocalDate oldStartDate = existingBudget.getStartDate();
        LocalDate oldEndDate = existingBudget.getEndDate();

        LocalDate newStartDate = budget.getStartDate();
        LocalDate newEndDate = budget.getEndDate();

        existingBudget.setName(budget.getName());
        existingBudget.setAmount(budget.getAmount());
        existingBudget.setStartDate(budget.getStartDate());
        existingBudget.setEndDate(budget.getEndDate());
        existingBudget.setDescription(budget.getDescription());

        Budget updatedBudget = budgetRepository.save(existingBudget);

        if(!oldStartDate.isEqual(newStartDate) || !oldEndDate.isEqual(newEndDate))
            budgetCalculatingService.reformatBudgetDays(updatedBudget);

        budgetCalculatingService.calculateBudget(updatedBudget);

        return updatedBudget;
    }

    @Override
    @Transactional
    public void deleteById(Long user_id, Long budget_id) {
        User user = userService.findById(user_id);
        budgetRepository.deleteById(user.getBudgets().stream()
                .filter(b -> b.getId().equals(budget_id))
                .findFirst()
                .orElseThrow(() -> new DatabaseEntityNotFoundException("Budget not found"))
                .getId());
    }

    private boolean isBudgetDatesNotValid(Budget budget) {
        return budget.getEndDate().isBefore(budget.getStartDate());
    }
}
