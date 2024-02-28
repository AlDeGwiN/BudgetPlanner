package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.model.Income;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetDTO {
    private Long id;
    @NotNull(message = "Budget name cannot be empty")
    @NotBlank(message = "")
    private String name;
    @Min(value = 0, message = "Budget amount cannot be less than zero")
    @NotNull(message = "Budget amount cannot be empty")
    private BigDecimal budgetAmount;
    @NotNull(message = "Start date cannot be empty")
    private LocalDate startDate;
    @NotNull(message = "End date cannot be empty")
    private LocalDate endDate;
    private String description;
    private List<Income> incomes;
    private List<Expense> expenses;
}
