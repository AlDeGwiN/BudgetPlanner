package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.BudgetDay;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.BudgetDayRepository;
import com.aldegwin.budgetplanner.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetDayServiceImplTest {
    @Mock
    private BudgetDayRepository budgetDayRepository;
    @InjectMocks
    private BudgetDayServiceImpl budgetDayService;
    private BudgetDay budgetDay;
    private Budget budget;
    @BeforeEach
    public void initialize() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .username("TestUser")
                .password("123456789")
                .budgets(Collections.emptyList())
                .lastLoginDate(LocalDateTime.now())
                .build();

        budget = Budget.builder()
                .id(1L)
                .user(user)
                .name("budget")
                .amount(new BigDecimal(100))
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024, 1, 5))
                .description("budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(Collections.emptyList())
                .build();

        budgetDay = BudgetDay.builder()
                .id(null)
                .budget(budget)
                .dayDate(LocalDate.of(2024, 1, 3))
                .amount(budget.getAmount())
                .description("BudgetDay description")
                .build();
    }

    @Test
    void givenBudgetDay_whenSave_returnBudgetDay() {
        when(budgetDayRepository.save(same(budgetDay))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(1L);
            return bd;
        });

        BudgetDay expected = BudgetDay.builder()
                .id(1L)
                .budget(budget)
                .dayDate(LocalDate.of(2024, 1, 3))
                .amount(budget.getAmount())
                .description("BudgetDay description")
                .build();

        BudgetDay result = budgetDayService.save(budgetDay);

        assertEquals(expected, result);
        verify(budgetDayRepository, times(1)).save(same(budgetDay));
    }

    @Test
    void givenBudgetDay_whenSave_throwsIdConflict_idNotNull() {
        budgetDay.setId(1L);
        IdConflictException e = assertThrows(IdConflictException.class, () -> budgetDayService.save(budgetDay));
        String expectedExceptionMessage = "Budget day ID must be null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenBudgetDay_whenUpdate_returnBudgetDay() {
        budgetDay.setId(1L);

        BudgetDay existingBudget =  BudgetDay.builder()
                .id(1L)
                .budget(budget)
                .dayDate(LocalDate.of(2024, 1, 5))
                .amount(new BigDecimal(123))
                .description("old BudgetDay description")
                .build();

        BudgetDay expected = BudgetDay.builder()
                .id(1L)
                .budget(budget)
                .dayDate(LocalDate.of(2024, 1, 3))
                .amount(budget.getAmount())
                .description("BudgetDay description")
                .build();

        when(budgetDayRepository.findById(1L)).thenReturn(Optional.of(existingBudget));
        when(budgetDayRepository.save(same(existingBudget))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            BudgetDay updatedBudgetDay = BudgetDay.builder().id(1L).budget(budget).build();
            updatedBudgetDay.setDayDate(bd.getDayDate());
            updatedBudgetDay.setAmount(bd.getAmount());
            updatedBudgetDay.setDescription(bd.getDescription());
            return updatedBudgetDay;
        });

        BudgetDay result = budgetDayService.update(budgetDay);

        assertEquals(expected, result);
        verify(budgetDayRepository, times(1)).save(same(existingBudget));
        verify(budgetDayRepository, times(1)).findById(1L);
    }

    @Test
    void givenBudgetDay_whenUpdate_throwsIdConflict_idIsNull() {
        IdConflictException e = assertThrows(IdConflictException.class, () -> budgetDayService.update(budgetDay));
        String expectedExceptionMessage = "Budget day ID must be not null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenBudgetDay_whenUpdate_throwsDatabaseBotFound_budgetDayNotFound() {
        budgetDay.setId(1L);
        when(budgetDayRepository.findById(1L)).thenReturn(Optional.empty());
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> budgetDayService.update(budgetDay));
        String expectedExceptionMessage = "Budget day not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetDayRepository, times(1)).findById(1L);
    }
    @Test
    void deleteById() {
        doNothing().when(budgetDayRepository).deleteById(any(Long.class));
        budgetDayService.deleteById(1L);
        verify(budgetDayRepository, times(1)).deleteById(1L);
    }
}