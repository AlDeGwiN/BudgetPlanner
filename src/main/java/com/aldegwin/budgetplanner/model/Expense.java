package com.aldegwin.budgetplanner.model;

import com.aldegwin.budgetplanner.model.constant.ExpenseType;
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
@Table(name = "expense")
public class Expense {
    @Id
    @Column(name = "expense_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "expense_type")
    private ExpenseType expenseType;
}
