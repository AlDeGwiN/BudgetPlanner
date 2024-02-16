package com.aldegwin.budgetplanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budget_day")
public class BudgetDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_day_id")
    private Long id;

    @Column(name = "day_date")
    private LocalDate dayDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;
}
