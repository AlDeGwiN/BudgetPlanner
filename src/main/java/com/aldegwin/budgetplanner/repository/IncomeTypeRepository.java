package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.IncomeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeTypeRepository extends JpaRepository<IncomeType, Long> {
}
