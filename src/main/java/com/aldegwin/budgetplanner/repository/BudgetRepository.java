package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
