package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
}
