package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.IncomeType;
import com.aldegwin.budgetplanner.repository.IncomeTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncomeTypeService {
    private final IncomeTypeRepository incomeTypeRepository;

    public IncomeTypeService(IncomeTypeRepository incomeTypeRepository) {
        this.incomeTypeRepository = incomeTypeRepository;
    }

    public Optional<IncomeType> findById(Long id) {
        return incomeTypeRepository.findById(id);
    }

    public Iterable<IncomeType> findAll() {
        return incomeTypeRepository.findAll();
    }
}
