package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
}
