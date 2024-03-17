package com.aldegwin.budgetplanner.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetDayDTO {
    private Long id;
    private LocalDate localDate;
    private BigDecimal amount;
    private String description;
}
