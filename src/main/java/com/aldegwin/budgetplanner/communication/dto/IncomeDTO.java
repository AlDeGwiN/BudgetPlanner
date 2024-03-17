package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.constant.IncomeType;
import com.aldegwin.budgetplanner.util.annotations.ValidEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeDTO {
    private Long id;

    @Min(value = 1, message = "The income must be greater than or equal to 1")
    @NotNull(message = "Income amount cannot be null")
    private BigDecimal amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate incomeDate;

    private String description;

    @NotNull(message = "The income type cannot be null")
    @ValidEnum(enumClass = IncomeType.class, message = "Incorrect income type")
    private String incomeType;
}
