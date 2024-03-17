package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import com.aldegwin.budgetplanner.service.BudgetCalculatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {
    @Mock
    private UserServiceImpl userService;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetCalculatingService budgetCalculatingService;
    @InjectMocks
    private BudgetServiceImpl budgetService;

    private Budget budget;
    private User user;

    @BeforeEach
    public void initialize() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now())
                .build();

        budget = Budget.builder()
                .id(null)
                .user(null)
                .name("budget")
                .amount(new BigDecimal(100))
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024, 1, 31))
                .description("budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(Collections.emptyList())
                .build();
    }

    @Test
    void givenUserIdAndBudget_whenSave_returnSavedBudget() {
        List<BudgetDay> budgetDays = new ArrayList<>();
        long budgetDayId = 1L;
        LocalDate endDate = budget.getEndDate();
        LocalDate dateIterator = LocalDate.from(budget.getStartDate());
        while(dateIterator.isBefore(endDate.plusDays(1))) {
            BudgetDay budgetDay = BudgetDay.builder()
                    .id(budgetDayId)
                    .budget(budget)
                    .dayDate(dateIterator)
                    .amount(budget.getAmount())
                    .description("Balance on " + dateIterator)
                    .build();
            budgetDayId += 1L;
            dateIterator = dateIterator.plusDays(1);
            budgetDays.add(budgetDay);
        }

        Budget expected = Budget.builder()
                .id(1L)
                .user(user)
                .name("budget")
                .amount(new BigDecimal(100))
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024, 1, 31))
                .description("budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(budgetDays)
                .build();


        when(userService.findById(1L)).thenReturn(user);
        when(budgetRepository.save(same(budget))).thenAnswer(invocation -> {
                    Budget b = invocation.getArgument(0, Budget.class);
                    b.setId(1L);
                    return b;
                });

        when(budgetCalculatingService.createBudgetDays(same(budget))).thenReturn(budgetDays);
        Budget result = budgetService.save(1L, budget);

        assertThat(result).isNotNull();
        assertEquals(expected, result);
        assertEquals(budgetDays, budget.getBudgetDays());
        verify(userService, times(1)).findById(1L);
        verify(budgetRepository, times(1)).save(same(budget));
        verify(budgetCalculatingService, times(1)).createBudgetDays(same(budget));
    }

    @Test
    void givenUserIdAndBudget_whenSave_throwsIdConflict_budgetIdNotNull() {
        budget.setId(1L);
        IdConflictException e =
                assertThrows(IdConflictException.class, () -> budgetService.save(1L, budget));
        String expectedExceptionMessage = "Budget ID must be null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdAndBudget_whenSave_throwsIncorrectDate_startDateBeforeEndDate() {
        budget.setUser(user);
        budget.setStartDate(LocalDate.of(2024, 2, 1));
        budget.setEndDate(LocalDate.of(2024,1,1));

        IncorrectDateException e =
                assertThrows(IncorrectDateException.class, () -> budgetService.save(1L, budget));
        String expectedExceptionMessage = "Incorrect budget period";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdAndBudget_whenSave_throwsDatabaseEntityNotFound_userNotFound() {
        when(userService.findById(1L)).thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> budgetService.save(1L, budget));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserIdAndBudgetId_whenFindById_returnBudget() {
        Budget expected = Budget.builder()
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

        budget.setId(1L);
        budget.setUser(user);

        List<Budget> userBudgets = createBudgets(2, 3, 4, 5, 6, 7, 8, 9, 10);
        userBudgets.forEach(b -> b.setUser(user));
        userBudgets.add(budget);

        user.setBudgets(userBudgets);

        when(userService.findById(1L)).thenReturn(user);

        Budget result = budgetService.findById(1L, 1L);

        assertThat(result).isNotNull();
        assertEquals(expected, result);
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserIdAndBudgetId_whenFindById_throwsDatabaseEntityFound_budgetNotFound() {
        List<Budget> userBudgets = createBudgets(2,3,4,5,6,10,12,14);
        userBudgets.forEach(b -> b.setUser(user));
        user.setBudgets(userBudgets);

        when(userService.findById(1L)).thenReturn(user);

        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class,
                        () -> budgetService.findById(1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserIdAndBudgetId_whenFindById_throwsDatabaseEntityNotFound_userNotFound() {
        when(userService.findById(1L)).thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> budgetService.findById(1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e. getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserId_whenFindAll_thenReturnBudgets() {
        List<Budget> userBudgets = createBudgets(1, 2, 3, 5, 6, 7, 8, 9, 10);
        userBudgets.forEach(b -> b.setUser(user));

        List<Budget> expected = createBudgets(1, 2, 3, 5, 6, 7, 8, 9, 10);
        expected.forEach(b -> b.setUser(user));

        user.setBudgets(userBudgets);

        when(userService.findById(1L)).thenReturn(user);

        Iterable<Budget> result = budgetService.findAll(1L);
        assertEquals(userBudgets, result);
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserId_whenFindById_throwsDatabaseEntityNotFound_userNotFound() {
        when(userService.findById(1L)).thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e =
                assertThrows(DatabaseEntityNotFoundException.class, () -> budgetService.findAll(1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    @Disabled
    void givenUserIdAndBudget_whenUpdate_returnBudget() {
        budget.setId(1L);

        Budget existingBudget = Budget.builder()
                .id(1L)
                .user(user)
                .name("old_budget")
                .amount(new BigDecimal(200))
                .startDate(LocalDate.of(2023,1,1))
                .endDate(LocalDate.of(2023, 1, 31))
                .description("old_budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(Collections.emptyList())
                .build();

        user.setBudgets(List.of(existingBudget));

        Budget expected = Budget.builder()
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


        when(userService.findById(1L)).thenReturn(user);
        when(budgetRepository.save(same(existingBudget))).thenAnswer(invocation -> {
            Budget b = invocation.getArgument(0, Budget.class);
            Budget updated = Budget.builder().id(user.getId()).build();
            updated.setUser(user);
            updated.setName(b.getName());
            updated.setAmount(b.getAmount());
            updated.setStartDate(b.getStartDate());
            updated.setEndDate(b.getEndDate());
            updated.setDescription(b.getDescription());
            updated.setIncomes(b.getIncomes());
            updated.setExpenses(b.getExpenses());
            updated.setBudgetDays(b.getBudgetDays());
            return updated;
        });

        Budget result = budgetService.update(1L, budget);

        assertEquals(expected, result);
        verify(userService, times(1)).findById(1L);
        verify(budgetRepository, times(1)).save(same(existingBudget));
    }

    @Test
    void givenUserIdAndBudget_whenUpdate_throwsIdConflict_budgetIdIsNull() {
        budget.setUser(user);
        IdConflictException e = assertThrows(IdConflictException.class, () -> budgetService.update(1L, budget));
        String expectedExceptionMessage = "Budget ID must be not null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdAndBudget_whenUpdate_throwsIncorrectDate_startDateAfterEndDate() {
        budget.setId(1L);
        budget.setUser(user);
        budget.setStartDate(LocalDate.of(2024, 2, 1));
        budget.setEndDate(LocalDate.of(2024,1,1));
        IncorrectDateException e =
                assertThrows(IncorrectDateException.class, () -> budgetService.update(1L, budget));
        String expectedExceptionMessage = "Incorrect budget period";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdAndBudgetId_whenDeleteById_thenReturnNothing() {
        budget.setId(1L);
        budget.setUser(user);

        List<Budget> userBudgets = createBudgets(2, 3, 4, 5, 6, 7, 8, 9, 10);
        userBudgets.forEach(b -> b.setUser(user));
        userBudgets.add(budget);

        user.setBudgets(userBudgets);

        when(userService.findById(1L)).thenReturn(user);
        doNothing().when(budgetRepository).deleteById(1L);

        budgetService.deleteById(1L, 1L);

        verify(userService, times(1)).findById(1L);
        verify(budgetRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenUserIdAndBudgetId_whenDeleteById_throwsDatabaseEntityNotFound_userNotFound() {
        when(userService.findById(1L)).thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e =  assertThrows(DatabaseEntityNotFoundException.class,
                () -> budgetService.deleteById(1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenUserIdAndBudgetId_whenDeleteById_throwsDatabaseEntityNotFound_budgetNotFound() {
        budget.setId(1L);

        List<Budget> userBudgets = createBudgets(2, 3, 4, 5, 6, 7, 8, 9, 10);
        userBudgets.forEach(b -> b.setUser(user));
        user.setBudgets(userBudgets);

        when(userService.findById(1L)).thenReturn(user);

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> budgetService.deleteById(1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void givenBudget_whenIsBudgetDatesNotValid_thenReturnFalse()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = budgetService.getClass().getDeclaredMethod("isBudgetDatesNotValid", Budget.class);
        method.setAccessible(true);
        assertEquals(false, method.invoke(budgetService, budget));
    }

    @Test
    void givenBudget_whenIsBudgetDatesNotValid_thenReturnTrue()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        budget.setStartDate(LocalDate.of(2024, 2, 1));
        budget.setEndDate(LocalDate.of(2024, 1, 1));
        Method method = budgetService.getClass().getDeclaredMethod("isBudgetDatesNotValid", Budget.class);
        method.setAccessible(true);
        assertEquals(true, method.invoke(budgetService, budget));
    }

    private List<Budget> createBudgets(long... identifiers) {
        List<Budget> budgets = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        for (long id : identifiers) {
            Budget b = Budget.builder()
                    .id(id)
                    .name(String.format("Budget %s", id))
                    .startDate(startDate)
                    .endDate(endDate)
                    .description(String.format("Description %s", id))
                    .incomes(Collections.emptyList())
                    .expenses(Collections.emptyList())
                    .amount((new BigDecimal(100).add(new BigDecimal(id))))
                    .budgetDays(Collections.emptyList())
                    .build();
            budgets.add(b);
            startDate = startDate.plusMonths(1);
            endDate = endDate.plusMonths(1);
        }
        return budgets;
    }
}