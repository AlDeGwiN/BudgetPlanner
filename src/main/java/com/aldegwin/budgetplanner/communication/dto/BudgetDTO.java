package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.Expense;
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

    @NotNull(message = "Budget name cannot be null")
    @NotBlank(message = "Budget name cannot be empty")
    private String name;

    @Min(value = 0, message = "Budget amount cannot be less than zero")
    @NotNull(message = "Budget amount cannot be empty")
    private BigDecimal amount;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    private String description;

    private List<IncomeDTO> incomes;

    private List<Expense> expenses;
}
