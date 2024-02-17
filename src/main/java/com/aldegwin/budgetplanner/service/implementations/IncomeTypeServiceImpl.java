package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.model.IncomeType;
import com.aldegwin.budgetplanner.repository.IncomeTypeRepository;
import com.aldegwin.budgetplanner.service.IncomeTypeService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncomeTypeServiceImpl implements IncomeTypeService {
    private final IncomeTypeRepository incomeTypeRepository;

    public IncomeTypeServiceImpl(IncomeTypeRepository incomeTypeRepository) {
        this.incomeTypeRepository = incomeTypeRepository;
    }

    @Override
    public IncomeType findById(Long id) {
        Optional<IncomeType> optionalIncome = incomeTypeRepository.findById(id);

        if(optionalIncome.isEmpty())
            throw new DatabaseEntityNotFoundException("Income type not found");

        return optionalIncome.get();
    }

    @Override
    public Iterable<IncomeType> findAll() {
        return incomeTypeRepository.findAll();
    }
}
