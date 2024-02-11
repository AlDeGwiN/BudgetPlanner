package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.model.ExpenseType;
import com.aldegwin.budgetplanner.repository.ExpenseTypeRepository;
import com.aldegwin.budgetplanner.service.ExpenseTypeService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    private final ExpenseTypeRepository expenseTypeRepository;

    public ExpenseTypeServiceImpl(ExpenseTypeRepository expenseTypeRepository) {
        this.expenseTypeRepository = expenseTypeRepository;
    }

    @Override
    public Optional<ExpenseType> findById(Long id) {
        return expenseTypeRepository.findById(id);
    }

    @Override
    public Iterable<ExpenseType> findAll() {
        return expenseTypeRepository.findAll();
    }
}
