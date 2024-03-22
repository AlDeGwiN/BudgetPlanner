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
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class Budget {
    @Id
    @Column(name = "budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_name")
    private String name;

    @Column(name = "budget_amount")
    private BigDecimal amount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @OrderBy("incomeDate asc")
    private List<Income> incomes;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @OrderBy("expenseDate asc")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    @OrderBy("dayDate asc")
    private List<BudgetDay> budgetDays;
}
