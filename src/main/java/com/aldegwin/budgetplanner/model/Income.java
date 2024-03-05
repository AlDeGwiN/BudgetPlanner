package com.aldegwin.budgetplanner.model;

import com.aldegwin.budgetplanner.model.constant.IncomeType;
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
@Table(name = "income")
public class Income {
    @Id
    @Column(name = "income_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "income_date")
    private LocalDate incomeDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "income_type")
    private IncomeType incomeType;
}
