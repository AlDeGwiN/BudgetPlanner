package com.aldegwin.budgetplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budget")
public class Budget {
    @Id
    @Column(name = "budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_name")
    private String name;

    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    @JoinColumn(name = "budget_id")
    private List<Income> incomes;

    @OneToMany
    @JoinColumn(name = "budget_id")
    private List<Expense> expenses;

    @OneToMany
    @JoinColumn(name = "budget_id")
    private List<BudgetDay> budgetDays;
}
