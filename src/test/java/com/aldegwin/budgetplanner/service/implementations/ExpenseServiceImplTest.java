package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.model.constant.ExpenseType;
import com.aldegwin.budgetplanner.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {
    @Mock
    private BudgetServiceImpl budgetService;
    @Mock
    private ExpenseRepository expenseRepository;
    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User user;

    private Budget budget;

    private Expense expense;

    @BeforeEach
    public void initialize() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(null)
                .lastLoginDate(LocalDateTime.now())
                .build();

        budget = Budget.builder()
                .id(1L)
                .user(user)
                .name("budget")
                .amount(new BigDecimal(100))
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024, 1, 31))
                .description("budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(Collections.emptyList())
                .build();

        user.setBudgets(List.of(budget));

        expense = Expense.builder()
                .id(null)
                .budget(null)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.TRAVEL)
                .expenseDate(LocalDate.of(2024, 1, 3))
                .description("Expense description")
                .build();
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_thenReturnExpense() {
        Expense expected = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.TRAVEL)
                .expenseDate(LocalDate.of(2024, 1, 3))
                .description("Expense description")
                .build();

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        when(expenseRepository.save(same(expense))).thenAnswer(invocation -> {
            Expense e = invocation.getArgument(0, Expense.class);
            e.setId(1L);
            return e;
        });

        Expense result = expenseService.save(1L, 1L, expense);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
        verify(expenseRepository, times(1)).save(same(expense));
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_throwsIdConflictException_expenseIdNotNull() {
        expense.setId(1L);
        IdConflictException e = assertThrows(IdConflictException.class,
                () -> expenseService.save(1L, 1L, expense));
        String expectedExceptionMessage = "Expense ID must be null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_throwsDatabaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.save(1L, 1L, expense));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_throwsDatabaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.save(1L, 1L, expense));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_throwsIncorrectDate_expenseDateAfterBudgetPeriod() {
        expense.setExpenseDate(LocalDate.of(2025,1,1));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> expenseService.save(1L, 1L, expense));
        String expectedErrorMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedErrorMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpense_whenSave_throwsIncorrectDate_expenseDateBeforeBudgetPeriod() {
        expense.setExpenseDate(LocalDate.of(2023,1,1));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> expenseService.save(1L, 1L, expense));
        String expectedErrorMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedErrorMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenFindById_returnExpense() {
        expense.setId(1L);
        expense.setBudget(budget);

        Expense expected = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.TRAVEL)
                .expenseDate(LocalDate.of(2024, 1, 3))
                .description("Expense description")
                .build();

        List<Expense> expenses = createExpenses(2, 3, 4, 5, 6, 7, 8, 9, 10);
        expenses.forEach(i -> i.setBudget(budget));
        expenses.add(expense);

        budget.setExpenses(expenses);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        Expense result = expenseService.findById(1L, 1L, 1L);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenFindById_throwsDataBaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenFindById_throwsDataBaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenFindById_throwsDataBaseEntityNotFound_expenseNotFound() {
        List<Expense> expenses = createExpenses(2, 3, 4, 5, 6, 7, 8, 9, 10);
        expenses.forEach(i -> i.setBudget(budget));

        budget.setExpenses(expenses);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "Expense not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_returnExpenses() {
        List<Expense> expenses = createExpenses(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        expenses.forEach(i -> i.setBudget(budget));

        List<Expense> expected = createExpenses(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        expected.forEach(i -> i.setBudget(budget));

        budget.setExpenses(expenses);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        List<Expense> result = (List<Expense>) expenseService.findAll(1L, 1L);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_throwsDataBaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.findAll(1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_throwsDataBaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.findAll(1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpense_whenUpdate_returnExpense() {
        expense.setId(1L);

        Expense existingExpense = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(300))
                .expenseType(ExpenseType.GROCERIES)
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("old Expense description")
                .build();

        budget.setExpenses(List.of(existingExpense));

        Expense expected = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.TRAVEL)
                .expenseDate(LocalDate.of(2024, 1, 3))
                .description("Expense description")
                .build();

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        when(expenseRepository.save(same(existingExpense))).thenAnswer(invocation -> {
            Expense e = invocation.getArgument(0, Expense.class);
            Expense updated = Expense.builder().id(e.getId()).build();

            updated.setBudget(e.getBudget());
            updated.setDescription(e.getDescription());
            updated.setAmount(e.getAmount());
            updated.setExpenseDate(e.getExpenseDate());
            updated.setExpenseType(e.getExpenseType());

            return updated;
        });

        Expense result = expenseService.update(1L, 1L, expense);

        assertEquals(expected, result);
        verify(budgetService,times(1)).findById(1L, 1L);
        verify(expenseRepository, times(1)).save(same(existingExpense));
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenUpdate_throwsIdConflict_expenseIdIsNull() {
        expense.setBudget(budget);
        IdConflictException e = assertThrows(IdConflictException.class,
                () -> expenseService.update(1L, 1L, expense));
        String expectedExceptionMessage = "Expense ID must be not null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenUpdate_throwsDatabaseEntityNotFound_userNotFound() {
        expense.setId(1L);
        expense.setBudget(budget);
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.update(1L, 1L, expense));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenUpdate_throwsDatabaseEntityNotFound_budgetNotFound() {
        expense.setId(1L);
        expense.setBudget(budget);
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.update(1L, 1L, expense));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenUpdate_throwsIncorrectDate_expenseDateBeforeBudgetPeriod() {
        expense.setId(1L);
        expense.setExpenseDate(LocalDate.of(2022,1,1));

        Expense existingExpense = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(300))
                .expenseType(ExpenseType.EDUCATION)
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("old Expense description")
                .build();

        budget.setExpenses(List.of(existingExpense));

        when(budgetService.findById(1L,1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> expenseService.update(1L, 1L, expense));
        String expectedExceptionMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenUpdate_throwsIncorrectDate_expenseDateAfterBudgetPeriod() {
        expense.setId(1L);
        expense.setExpenseDate(LocalDate.of(2026,1,1));

        Expense existingExpense = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(300))
                .expenseType(ExpenseType.EDUCATION)
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("old Expense description")
                .build();

        budget.setExpenses(List.of(existingExpense));

        when(budgetService.findById(1L,1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> expenseService.update(1L, 1L, expense));
        String expectedExceptionMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenDeleteById_returnNothing() {
        expense.setId(1L);
        expense.setBudget(budget);

        budget.setExpenses(List.of(expense));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        doNothing().when(expenseRepository).deleteById(1L);

        expenseService.deleteById(1L, 1L, 1L);

        verify(budgetService, times(1)).findById(1L, 1L);
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenDeleteById_throwsDatabaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.deleteById(1L, 1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenDeleteById_throwsDatabaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.deleteById(1L, 1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdExpenseId_whenDeleteById_throwsDatabaseEntityNotFound_expenseNotFound() {
        List<Expense> expenses = createExpenses(2, 3, 4, 5, 6, 7, 8, 9, 10);
        budget.setExpenses(expenses);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> expenseService.deleteById(1L, 1L, 1L));
        String expectedExceptionMessage = "Expense not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenBudget_whenDeleteUnnecessaryExpensesForBudget_returnNothing() {
        Expense expenseBeforeBudgetPeriod = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.ENTERTAINMENT)
                .expenseDate(LocalDate.of(2022, 1, 3))
                .description("Expense before budget period")
                .build();

        Expense expenseInBudgetPeriod = Expense.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.DINING_OUT)
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("Expense in budget period")
                .build();

        Expense expenseAfterBudgetPeriod = Expense.builder()
                .id(3L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.RENT)
                .expenseDate(LocalDate.of(2026, 1, 15))
                .description("Expense after budget period")
                .build();

        List<Expense> expectedExpenses = List.of(Expense.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .expenseType(ExpenseType.DINING_OUT)
                .expenseDate(LocalDate.of(2024, 1, 15))
                .description("Expense in budget period")
                .build());

        budget.setExpenses(List.of(expenseAfterBudgetPeriod, expenseBeforeBudgetPeriod, expenseInBudgetPeriod));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        doNothing().when(expenseRepository).deleteById(any(Long.class));

        expenseService.deleteUnnecessaryExpensesForBudget(budget);

        assertEquals(expectedExpenses, budget.getExpenses());
        verify(expenseRepository, times(1)).deleteById(1L);
        verify(expenseRepository, never()).deleteById(2L);
        verify(expenseRepository, times(1)).deleteById(3L);
    }

    @Test
    void givenExpenseBudget_whenIsExpenseDateNotValid_returnTrue()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        expense.setExpenseDate(LocalDate.of(2026, 1,1));

        Method method = expenseService.getClass()
                .getDeclaredMethod("isExpenseDateNotValid", Expense.class, Budget.class);
        method.setAccessible(true);

        boolean expected = true;

        assertEquals(expected, method.invoke(expenseService, expense, budget));
    }

    @Test
    void givenExpenseBudget_whenIsExpenseDateNotValid_returnFalse()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = expenseService.getClass()
                .getDeclaredMethod("isExpenseDateNotValid", Expense.class, Budget.class);
        method.setAccessible(true);

        boolean expected = false;

        assertEquals(expected, method.invoke(expenseService, expense, budget));
    }

    private List<Expense> createExpenses(long... identifiers) {
        List<Expense> expenses = new ArrayList<>();
        LocalDate expenseDate = LocalDate.of(2024, 1, 1);
        for(long id : identifiers) {
            Expense e = Expense.builder()
                    .id(id)
                    .expenseType(ExpenseType.TRAVEL)
                    .expenseDate(expenseDate)
                    .description("Expense description " + id)
                    .amount((new BigDecimal(100)).add(new BigDecimal(id)))
                    .build();
            expenses.add(e);
            expenseDate = expenseDate.plusDays(1);
        }
        return expenses;
    }
}