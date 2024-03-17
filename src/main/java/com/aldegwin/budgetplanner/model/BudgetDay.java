package com.aldegwin.budgetplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budget_day")
@ToString(exclude = "budget")
@EqualsAndHashCode(exclude = "budget")
public class BudgetDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_day_id")
    private Long id;

    @Column(name = "day_date")
    private LocalDate dayDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Column(name = "description")
    private String description;
}
