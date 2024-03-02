package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.ExpenseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;

    @Min(value = 1, message = "The expense must be greater than or equal to 1")
    @NotNull(message = "Expense amount cannot be null")
    private BigDecimal amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    private String description;

    @NotNull(message = "The expense type cannot be null")
    private ExpenseType expenseType;
}
