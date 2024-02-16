package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.BudgetDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetDayRepository extends JpaRepository<BudgetDay, Long> {
}
