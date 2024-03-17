package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.service.BudgetDayService;
import com.aldegwin.budgetplanner.service.BudgetCalculatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetCalculatingServiceImpl implements BudgetCalculatingService {
    private final BudgetDayService budgetDayService;
    @Override
    @Transactional
    public List<BudgetDay> createBudgetDays(Budget budget) {
        List<BudgetDay> budgetDays = new ArrayList<>();

        LocalDate startDate = budget.getStartDate();
        LocalDate endDate = budget.getEndDate();

        LocalDate dateIterator = LocalDate.from(startDate);
        while (dateIterator.isBefore(endDate.plusDays(1))) {
            BudgetDay budgetDay = BudgetDay.builder()
                    .id(null)
                    .budget(budget)
                    .dayDate(dateIterator)
                    .amount(budget.getAmount())
                    .description("Balance on " + dateIterator)
                    .build();
            budgetDays.add(budgetDay);
            budgetDayService.save(budgetDay);
            dateIterator = dateIterator.plusDays(1);
        }

        budgetDays.sort((bd1, bd2) -> {
            LocalDate bd1Date = bd1.getDayDate();
            LocalDate bd2Date = bd2.getDayDate();
            if (bd1Date.isAfter(bd2Date))
                return 1;
            else if (bd1Date.isBefore(bd2Date))
                return -1;
            return 0;
        });

        return budgetDays;
    }

    @Override
    @Transactional
    public void calculateBudget(Budget budget) {
        List<BudgetDay> budgetDays = budget.getBudgetDays();

        List<Income> incomes = budget.getIncomes();
        List<Expense> expenses = budget.getExpenses();
        Map<LocalDate, BigDecimal> dayBalance = new HashMap<>();

        incomes.forEach(i -> {
            LocalDate incomeDate = i.getIncomeDate();
            dayBalance.put(incomeDate, dayBalance.getOrDefault(incomeDate, BigDecimal.ZERO)
                    .add(i.getAmount()));
        });

        expenses.forEach(e -> {
            LocalDate expenseDate = e.getExpenseDate();
            dayBalance.put(expenseDate, dayBalance.getOrDefault(expenseDate, BigDecimal.ZERO)
                    .subtract(e.getAmount()));
        });

        BigDecimal currentDayAmount = budget.getAmount();
        for(BudgetDay budgetDay : budgetDays) {
            currentDayAmount = currentDayAmount.add(dayBalance
                    .getOrDefault(budgetDay.getDayDate(), BigDecimal.ZERO));
            budgetDay.setAmount(currentDayAmount);
            budgetDayService.update(budgetDay);
        }
        budget.setBudgetDays(budgetDays);
    }

    @Override
    @Transactional
    public void reformatBudgetDays(Budget budget) {
        List<BudgetDay> budgetDays = budget.getBudgetDays();
        List<BudgetDay> newBudgetDays = new ArrayList<>();

        LocalDate budgetStartDate = budget.getStartDate();
        LocalDate budgetEndDate = budget.getEndDate();

        for (BudgetDay budgetDay : budgetDays) {
            LocalDate budgetDayDate = budgetDay.getDayDate();
            if (budgetDayDate.isAfter(budgetEndDate) || budgetDayDate.isBefore(budgetStartDate))
                budgetDayService.deleteById(budgetDay.getId());
            else
                newBudgetDays.add(budgetDay);
        }

        Set<LocalDate> existingBudgetDates = newBudgetDays
                .stream()
                .map(BudgetDay::getDayDate)
                .collect(Collectors.toSet());

        List<LocalDate> budgetDates = getAllDates(budget);
        for(LocalDate date : budgetDates) {
            if(!existingBudgetDates.contains(date)) {
                BudgetDay budgetDay = BudgetDay.builder()
                        .id(null)
                        .budget(budget)
                        .dayDate(date)
                        .amount(budget.getAmount())
                        .description("Balance on " + date)
                        .build();
                BudgetDay savedBudgetDay = budgetDayService.save(budgetDay);
                newBudgetDays.add(savedBudgetDay);
            }
        }

        newBudgetDays.sort((bd1, bd2) -> {
            LocalDate bd1Date = bd1.getDayDate();
            LocalDate bd2Date = bd2.getDayDate();
            if (bd1Date.isAfter(bd2Date))
                return 1;
            else if (bd1Date.isBefore(bd2Date))
                return -1;
            return 0;
        });

        budget.setBudgetDays(newBudgetDays);
    }

    private List<LocalDate> getAllDates(Budget budget) {
        List<LocalDate> dates = new ArrayList<>();

        LocalDate startDate = budget.getStartDate();
        LocalDate endDate = budget.getEndDate();

        LocalDate dateIterator = LocalDate.from(startDate);
        while (dateIterator.isBefore(endDate.plusDays(1))) {
            dates.add(dateIterator);
            dateIterator = dateIterator.plusDays(1);
        }

        return dates;
    }
}
