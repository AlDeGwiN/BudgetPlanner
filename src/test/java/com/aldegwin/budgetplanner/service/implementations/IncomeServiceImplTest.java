package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.exception.IncorrectDateException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.model.constant.IncomeType;
import com.aldegwin.budgetplanner.repository.IncomeRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {
    @Mock
    private BudgetServiceImpl budgetService;
    @Mock
    private IncomeRepository incomeRepository;
    @InjectMocks
    private IncomeServiceImpl incomeService;

    private Budget budget;

    private Income income;

    @BeforeEach
    public void initialize() {
        User user = User.builder()
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

        income = Income.builder()
                .id(null)
                .budget(null)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 3))
                .description("Income description")
                .build();
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_thenReturnIncome() {
        Income expected = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 3))
                .description("Income description")
                .build();

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        when(incomeRepository.save(same(income))).thenAnswer(invocation -> {
            Income i = invocation.getArgument(0, Income.class);
            i.setId(1L);
            return i;
        });

        Income result = incomeService.save(1L, 1L, income);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
        verify(incomeRepository, times(1)).save(same(income));
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_throwsIdConflictException_IncomeIdNotNull() {
        income.setId(1L);
        IdConflictException e = assertThrows(IdConflictException.class,
                () -> incomeService.save(1L, 1L, income));
        String expectedExceptionMessage = "Income ID must be null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_throwsDatabaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.save(1L, 1L, income));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_throwsDatabaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.save(1L, 1L, income));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_throwsIncorrectDate_incomeDateAfterBudgetPeriod() {
        income.setIncomeDate(LocalDate.of(2025,1,1));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> incomeService.save(1L, 1L, income));
        String expectedErrorMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedErrorMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncome_whenSave_throwsIncorrectDate_incomeDateBeforeBudgetPeriod() {
        income.setIncomeDate(LocalDate.of(2023,1,1));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> incomeService.save(1L, 1L, income));
        String expectedErrorMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedErrorMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenFindById_returnIncome() {
        income.setId(1L);
        income.setBudget(budget);

        Income expected = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 3))
                .description("Income description")
                .build();

        List<Income> incomes = createIncomes(2, 3, 4, 5, 6, 7, 8, 9, 10);
        incomes.forEach(i -> i.setBudget(budget));
        incomes.add(income);

        budget.setIncomes(incomes);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        Income result = incomeService.findById(1L, 1L, 1L);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenFindById_throwsDataBaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenFindById_throwsDataBaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenFindById_throwsDataBaseEntityNotFound_incomeNotFound() {
        List<Income> incomes = createIncomes(2, 3, 4, 5, 6, 7, 8, 9, 10);
        incomes.forEach(i -> i.setBudget(budget));

        budget.setIncomes(incomes);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.findById(1L, 1L, 1L));
        String expectedExceptionMessage = "Income not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_returnIncomes() {
        List<Income> incomes = createIncomes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        incomes.forEach(i -> i.setBudget(budget));

        List<Income> expected = createIncomes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        expected.forEach(i -> i.setBudget(budget));

        budget.setIncomes(incomes);

        when(budgetService.findById(1L, 1L)).thenReturn(budget);

        List<Income> result = (List<Income>) incomeService.findAll(1L, 1L);

        assertEquals(expected, result);
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_throwsDataBaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.findAll(1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetId_whenFindAll_throwsDataBaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));

        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.findAll(1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncome_whenUpdate_returnIncome() {
        income.setId(1L);

        Income existingIncome = Income.builder()
            .id(1L)
            .budget(budget)
            .amount(new BigDecimal(300))
            .incomeType(IncomeType.GRANTS)
            .incomeDate(LocalDate.of(2024, 1, 15))
            .description("old Income description")
            .build();

        budget.setIncomes(List.of(existingIncome));

        Income expected = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 3))
                .description("Income description")
                .build();

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        when(incomeRepository.save(same(existingIncome))).thenAnswer(invocation -> {
            Income i = invocation.getArgument(0, Income.class);
            Income updated = Income.builder().id(i.getId()).build();

            updated.setBudget(i.getBudget());
            updated.setDescription(i.getDescription());
            updated.setAmount(i.getAmount());
            updated.setIncomeDate(i.getIncomeDate());
            updated.setIncomeType(i.getIncomeType());

            return updated;
        });

        Income result = incomeService.update(1L, 1L, income);

        assertEquals(expected, result);
        verify(budgetService,times(1)).findById(1L, 1L);
        verify(incomeRepository, times(1)).save(same(existingIncome));
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenUpdate_throwsIdConflict_incomeIdIsNull() {
        income.setBudget(budget);
        IdConflictException e = assertThrows(IdConflictException.class,
                () -> incomeService.update(1L, 1L, income));
        String expectedExceptionMessage = "Income ID must be not null";
        assertEquals(expectedExceptionMessage, e.getMessage());
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenUpdate_throwsDatabaseEntityNotFound_userNotFound() {
        income.setId(1L);
        income.setBudget(budget);
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.update(1L, 1L, income));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenUpdate_throwsDatabaseEntityNotFound_budgetNotFound() {
        income.setId(1L);
        income.setBudget(budget);
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.update(1L, 1L, income));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenUpdate_throwsIncorrectDate_incomeDateBeforeBudgetPeriod() {
        income.setId(1L);
        income.setIncomeDate(LocalDate.of(2022,1,1));

        Income existingIncome = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(300))
                .incomeType(IncomeType.GRANTS)
                .incomeDate(LocalDate.of(2024, 1, 15))
                .description("old Income description")
                .build();

        budget.setIncomes(List.of(existingIncome));

        when(budgetService.findById(1L,1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> incomeService.update(1L, 1L, income));
        String expectedExceptionMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenUpdate_throwsIncorrectDate_incomeDateAfterBudgetPeriod() {
        income.setId(1L);
        income.setIncomeDate(LocalDate.of(2026,1,1));

        Income existingIncome = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(300))
                .incomeType(IncomeType.GRANTS)
                .incomeDate(LocalDate.of(2024, 1, 15))
                .description("old Income description")
                .build();

        budget.setIncomes(List.of(existingIncome));

        when(budgetService.findById(1L,1L)).thenReturn(budget);

        IncorrectDateException e = assertThrows(IncorrectDateException.class,
                () -> incomeService.update(1L, 1L, income));
        String expectedExceptionMessage = "The entered date is not included in the budget deadline";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenDeleteById_returnNothing() {
        income.setId(1L);
        income.setBudget(budget);

        budget.setIncomes(List.of(income));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        doNothing().when(incomeRepository).deleteById(1L);

        incomeService.deleteById(1L, 1L, 1L);

        verify(budgetService, times(1)).findById(1L, 1L);
        verify(incomeRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenDeleteById_throwsDatabaseEntityNotFound_userNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("User not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.deleteById(1L, 1L, 1L));
        String expectedExceptionMessage = "User not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenDeleteById_throwsDatabaseEntityNotFound_budgetNotFound() {
        when(budgetService.findById(1L, 1L))
                .thenThrow(new DatabaseEntityNotFoundException("Budget not found"));
        DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
                () -> incomeService.deleteById(1L, 1L, 1L));
        String expectedExceptionMessage = "Budget not found";
        assertEquals(expectedExceptionMessage, e.getMessage());
        verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenUserIdBudgetIdIncomeId_whenDeleteById_throwsDatabaseEntityNotFound_incomeNotFound() {
       List<Income> incomes = createIncomes(2, 3, 4, 5, 6, 7, 8, 9, 10);
       budget.setIncomes(incomes);

       when(budgetService.findById(1L, 1L)).thenReturn(budget);
       DatabaseEntityNotFoundException e = assertThrows(DatabaseEntityNotFoundException.class,
               () -> incomeService.deleteById(1L, 1L, 1L));
       String expectedExceptionMessage = "Income not found";
       assertEquals(expectedExceptionMessage, e.getMessage());
       verify(budgetService, times(1)).findById(1L, 1L);
    }

    @Test
    void givenBudget_whenDeleteUnnecessaryIncomesForBudget_returnNothing() {
        Income incomeBeforeBudgetPeriod = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2022, 1, 3))
                .description("Income before budget period")
                .build();

        Income incomeInBudgetPeriod = Income.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 15))
                .description("Income in budget period")
                .build();

        Income incomeAfterBudgetPeriod = Income.builder()
                .id(3L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2026, 1, 15))
                .description("Income after budget period")
                .build();

        List<Income> expectedIncomes = List.of(Income.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(100))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 15))
                .description("Income in budget period")
                .build());

        budget.setIncomes(List.of(incomeAfterBudgetPeriod, incomeBeforeBudgetPeriod, incomeInBudgetPeriod));

        when(budgetService.findById(1L, 1L)).thenReturn(budget);
        doNothing().when(incomeRepository).deleteById(any(Long.class));

        incomeService.deleteUnnecessaryIncomesForBudget(budget);

        assertEquals(expectedIncomes, budget.getIncomes());
        verify(incomeRepository, times(1)).deleteById(1L);
        verify(incomeRepository, never()).deleteById(2L);
        verify(incomeRepository, times(1)).deleteById(3L);
    }

    @Test
    void givenIncomeBudget_whenIsIncomeDateNotValid_returnTrue()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        income.setIncomeDate(LocalDate.of(2026, 1,1));

        Method method = incomeService.getClass()
                .getDeclaredMethod("isIncomeDateNotValid", Income.class, Budget.class);
        method.setAccessible(true);

        boolean expected = true;

        assertEquals(expected, method.invoke(incomeService, income, budget));
    }

    @Test
    void givenIncomeBudget_whenIsIncomeDateNotValid_returnFalse()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = incomeService.getClass()
                .getDeclaredMethod("isIncomeDateNotValid", Income.class, Budget.class);
        method.setAccessible(true);

        boolean expected = false;

        assertEquals(expected, method.invoke(incomeService, income, budget));
    }

    private List<Income> createIncomes(long... identifiers) {
        List<Income> incomes = new ArrayList<>();
        LocalDate incomeDate = LocalDate.of(2024, 1, 1);
        for(long id : identifiers) {
             Income i = Income.builder()
                    .id(id)
                    .incomeType(IncomeType.GIFTS)
                    .incomeDate(incomeDate)
                    .description("Income description " + id)
                    .amount((new BigDecimal(100)).add(new BigDecimal(id)))
                    .build();
             incomes.add(i);
             incomeDate = incomeDate.plusDays(1);
        }
        return incomes;
    }
}