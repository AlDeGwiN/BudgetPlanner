package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.*;
import com.aldegwin.budgetplanner.model.constant.ExpenseType;
import com.aldegwin.budgetplanner.model.constant.IncomeType;
import com.aldegwin.budgetplanner.service.BudgetDayService;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetCalculatingServiceImplTest {
    @Mock
    private BudgetDayService budgetDayService;
    @InjectMocks
    private BudgetCalculatingServiceImpl budgetCalculatingService;

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
                .endDate(LocalDate.of(2024, 1, 3))
                .description("budgetDescription")
                .incomes(Collections.emptyList())
                .expenses(Collections.emptyList())
                .budgetDays(Collections.emptyList())
                .build();
    }

    @Test
    void givenBudget_whenCreateBudgetDays_returnBudgetDays() {
        LocalDate firstBudgetDayDate =  LocalDate.from(budget.getStartDate());

        BudgetDay expectedBudgetDay1 = createBudgetDay(budget, 1L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay expectedBudgetDay2 = createBudgetDay(budget, 2L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay expectedBudgetDay3 = createBudgetDay(budget, 3L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> expected =
                List.of(expectedBudgetDay1, expectedBudgetDay2, expectedBudgetDay3);

        BudgetDay budgetDay1 = createBudgetDay(budget, null,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay budgetDay2 = createBudgetDay(budget, null,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay budgetDay3 = createBudgetDay(budget, null,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        when(budgetDayService.save(eq(budgetDay1))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(1L);
            return bd;
        });

        when(budgetDayService.save(eq(budgetDay2))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(2L);
            return bd;
        });

        when(budgetDayService.save(eq(budgetDay3))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(3L);
            return bd;
        });

        List<BudgetDay> result = budgetCalculatingService.createBudgetDays(budget);

        assertEquals(expected, result);
        verify(budgetDayService, times(3)).save(any(BudgetDay.class));
        verify(budgetDayService, times(1))
                .save(argThat(bd -> bd.getDayDate().isEqual(firstBudgetDayDate)));
        verify(budgetDayService, times(1))
                .save(argThat(bd -> bd.getDayDate().isEqual(firstBudgetDayDate
                        .plusDays(1))));
        verify(budgetDayService, times(1))
                .save(argThat(bd -> bd.getDayDate().isEqual(firstBudgetDayDate
                        .plusDays(2))));
    }

    @Test
    void givenBudget_whenCalculateBudget_returnNothing_withoutIncomesAndExpenses() {
        LocalDate firstBudgetDayDate =  LocalDate.from(budget.getStartDate());

        BudgetDay budgetDay1 = createBudgetDay(budget, 1L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay budgetDay2 = createBudgetDay(budget, 2L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay budgetDay3 = createBudgetDay(budget, 3L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> budgetDays = new ArrayList<>();
        budgetDays.add(budgetDay1);
        budgetDays.add(budgetDay2);
        budgetDays.add(budgetDay3);

        budget.setBudgetDays(budgetDays);

        BudgetDay expectedBudgetDay1 = createBudgetDay(budget, 1L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay expectedBudgetDay2 = createBudgetDay(budget, 2L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay expectedBudgetDay3 = createBudgetDay(budget, 3L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> expectedBudgetDays = List.of(expectedBudgetDay1, expectedBudgetDay2, expectedBudgetDay3);

        budgetCalculatingService.calculateBudget(budget);
        assertEquals(expectedBudgetDays, budget.getBudgetDays());
    }

    @Test
    void givenBudget_whenCalculateBudget_returnNothing_withIncomesAndExpenses() {
        Income income1 = Income.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(40))
                .incomeType(IncomeType.INTEREST)
                .incomeDate(LocalDate.of(2024, 1, 1))
                .description("Income 1")
                .build();

        Income income2 = Income.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(130))
                .incomeType(IncomeType.GIFTS)
                .incomeDate(LocalDate.of(2024, 1, 3))
                .description("Income 2")
                .build();

        budget.setIncomes(List.of(income1, income2));

        Expense expense1 = Expense.builder()
                .id(1L)
                .budget(budget)
                .amount(new BigDecimal(140))
                .expenseType(ExpenseType.TRAVEL)
                .expenseDate(LocalDate.of(2024, 1, 1))
                .description("Expense 1")
                .build();

        Expense expense2 = Expense.builder()
                .id(2L)
                .budget(budget)
                .amount(new BigDecimal(30))
                .expenseType(ExpenseType.INSURANCE)
                .expenseDate(LocalDate.of(2024, 1, 2))
                .description("Expense 2")
                .build();

        Expense expense3= Expense.builder()
                .id(3L)
                .budget(budget)
                .amount(new BigDecimal(85))
                .expenseType(ExpenseType.HEALTHCARE)
                .expenseDate(LocalDate.of(2024, 1, 3))
                .description("Expense 3")
                .build();

        budget.setExpenses(List.of(expense1, expense2, expense3));

        LocalDate firstBudgetDayDate =  LocalDate.from(budget.getStartDate());

        BudgetDay budgetDay1 = createBudgetDay(budget, 1L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay budgetDay2 = createBudgetDay(budget, 2L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay budgetDay3 = createBudgetDay(budget, 3L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> budgetDays = new ArrayList<>();
        budgetDays.add(budgetDay1);
        budgetDays.add(budgetDay2);
        budgetDays.add(budgetDay3);

        budget.setBudgetDays(budgetDays);

        BudgetDay expectedBudgetDay1 = createBudgetDay(budget, 1L,
                BigDecimal.ZERO, firstBudgetDayDate);
        BudgetDay expectedBudgetDay2 = createBudgetDay(budget, 2L,
                new BigDecimal(-30), firstBudgetDayDate.plusDays(1));
        BudgetDay expectedBudgetDay3 = createBudgetDay(budget, 3L,
                new BigDecimal(15), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> expectedBudgetDays = List.of(expectedBudgetDay1, expectedBudgetDay2, expectedBudgetDay3);

        budgetCalculatingService.calculateBudget(budget);
        assertEquals(expectedBudgetDays, budget.getBudgetDays());
    }

    @Test
    void givenBudget_whenReformatBudgetDays_returnNothing_extraBudgetDays() {
        List<BudgetDay> budgetDays = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 12, 26);
        for(long id = 1; id < 13; id++ ) {
            budgetDays.add(createBudgetDay(budget, id, new BigDecimal(100), date));
            date = date.plusDays(1);
        }

        budget.setBudgetDays(budgetDays);

        LocalDate firstBudgetDayDate = LocalDate.from(budget.getStartDate());
        BudgetDay expectedBudgetDay1 = createBudgetDay(budget, 7L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay expectedBudgetDay2 = createBudgetDay(budget, 8L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay expectedBudgetDay3 = createBudgetDay(budget, 9L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> expectedBudgetDays = List.of(expectedBudgetDay1, expectedBudgetDay2, expectedBudgetDay3);

        doNothing().when(budgetDayService).deleteById(any(Long.class));

        budgetCalculatingService.reformatBudgetDays(budget);

        assertEquals(expectedBudgetDays, budget.getBudgetDays());
        verify(budgetDayService, times(9)).deleteById(any(Long.class));
        verify(budgetDayService, never()).save(any(BudgetDay.class));
    }
    @Test
    void givenBudget_whenReformatBudgetDays_returnNothing_missingBudgetDaysAndExtraBudgetDays(){
        List<BudgetDay> budgetDays = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 12, 26);
        for(long id = 1; id < 8; id++) {
            budgetDays.add(createBudgetDay(budget, id, new BigDecimal(100), date));
            date = date.plusDays(1);
        }

        budget.setBudgetDays(budgetDays);

        LocalDate firstBudgetDayDate = LocalDate.from(budget.getStartDate());
        BudgetDay expectedBudgetDay1 = createBudgetDay(budget, 7L,
                new BigDecimal(100), firstBudgetDayDate);
        BudgetDay expectedBudgetDay2 = createBudgetDay(budget, 8L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(1));
        BudgetDay expectedBudgetDay3 = createBudgetDay(budget, 9L,
                new BigDecimal(100), firstBudgetDayDate.plusDays(2));

        List<BudgetDay> expectedBudgetDays = List.of(expectedBudgetDay1, expectedBudgetDay2, expectedBudgetDay3);

        doNothing().when(budgetDayService).deleteById(any(Long.class));

        BudgetDay budgetDayArg1 = BudgetDay.builder()
                .id(null)
                .budget(budget)
                .dayDate(firstBudgetDayDate.plusDays(1))
                .amount(new BigDecimal(100))
                .description("Balance on " + firstBudgetDayDate.plusDays(1))
                .build();

        BudgetDay budgetDayArg2 = BudgetDay.builder()
                .id(null)
                .budget(budget)
                .dayDate(firstBudgetDayDate.plusDays(2))
                .amount(new BigDecimal(100))
                .description("Balance on " + firstBudgetDayDate.plusDays(2))
                .build();

        when(budgetDayService.save(eq(budgetDayArg1))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(8L);
            return bd;
        });

        when(budgetDayService.save(eq(budgetDayArg2))).thenAnswer(invocation -> {
            BudgetDay bd = invocation.getArgument(0, BudgetDay.class);
            bd.setId(9L);
            return bd;
        });

        budgetCalculatingService.reformatBudgetDays(budget);

        assertEquals(expectedBudgetDays, budget.getBudgetDays());

        verify(budgetDayService, times(6)).deleteById(any(Long.class));
        verify(budgetDayService, times(2)).save(any(BudgetDay.class));
    }

    @Test
    void givenBudget_whenGetAllDates_returnBudgetDaysDates()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = budgetCalculatingService.getClass()
                .getDeclaredMethod("getAllDates", Budget.class);
        method.setAccessible(true);

        List<LocalDate> expectedDates = List.of(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 3));

        assertEquals(expectedDates, method.invoke(budgetCalculatingService, budget));
    }

    private BudgetDay createBudgetDay(Budget budget, Long id, BigDecimal amount, LocalDate date) {
        return BudgetDay.builder()
                .id(id)
                .amount(amount)
                .budget(budget)
                .description("Balance on " + date)
                .dayDate(date)
                .build();
    }
}