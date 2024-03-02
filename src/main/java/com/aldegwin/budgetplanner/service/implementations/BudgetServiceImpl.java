package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import com.aldegwin.budgetplanner.service.BudgetService;
import com.aldegwin.budgetplanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Budget save(Long user_id, Budget budget) {
        User user = userService.findById(user_id);

        if(isBudgetDatesNotValid(budget))
            throw new IncorrectDateException("Wrong date");

        budget.setUser(user);

        user.getBudgets().add(budget);

        budgetRepository.save(budget);
        userService.update(user);

        return budget;
    }

    @Override
    public Budget findById(Long user_id, Long budget_id) {
        User user = userService.findById(user_id);
        return user.getBudgets().stream()
                .filter(b -> b.getId().equals(budget_id))
                .findFirst()
                .orElseThrow(() -> new DatabaseEntityNotFoundException("Budget not found!"));
    }

    @Override
    public Iterable<Budget> findAllByUserId(Long id) {
        User user = userService.findById(id);
        return user.getBudgets();
    }

    @Override
    @Transactional
    public Budget update(Long user_id, Budget budget) {
        User user = userService.findById(user_id);

        if(isBudgetDatesNotValid(budget))
            throw new IncorrectDateException("Wrong date");

        user.setBudgets(user.getBudgets()
                .stream()
                .map(b -> b.getId().equals(budget.getId()) ? budget : b)
                .collect(Collectors.toList()));
        budget.setUser(user);
        userService.update(user);
        return budgetRepository.save(budget);
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
