package com.aldegwin.budgetplanner.controller;

import com.aldegwin.budgetplanner.communication.dto.ExpenseDTO;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.Expense;
import com.aldegwin.budgetplanner.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{user_id}/budgets/{budget_id}/expenses")
public class ExpenseRestController {
    private final ExpenseService expenseService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(@PathVariable("user_id") Long user_id,
                                                           @PathVariable("budget_id") Long budget_id) {
        List<ExpenseDTO> expenses = ((List<Expense>) expenseService.findAll(user_id, budget_id)).stream()
                .map(this::getExpenseDto)
                .toList();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(expenses);
    }

    @GetMapping("/{expense_id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable("user_id") Long user_id,
                                                     @PathVariable("budget_id") Long budget_id,
                                                     @PathVariable("expense_id") Long expense_id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getExpenseDto(expenseService.findById(user_id, budget_id, expense_id)));
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@PathVariable("user_id") Long user_id,
                                                    @PathVariable("budget_id") Long budget_id,
                                                    @RequestBody @Valid ExpenseDTO expenseDTO,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        if(expenseDTO.getId() != null)
            throw new IdConflictException("Expense ID must be null");

        Expense expense = expenseService.save(user_id, budget_id, getExpenseFromDto(expenseDTO));
        return ResponseEntity.created(
                uriComponentsBuilder.path("/users/{user_id}/budgets/{budget_id}/expenses/{expense_id}")
                        .build(Map.of("user_id", user_id,
                                "budget_id", budget_id,
                                "expense_id", expense.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(getExpenseDto(expense));

    }

    @PutMapping("/{expense_id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable("user_id") Long user_id,
                                                    @PathVariable("budget_id") Long budget_id,
                                                    @PathVariable("expense_id") Long expense_id,
                                                    @RequestBody @Valid ExpenseDTO expenseDTO) {
        if(!Objects.equals(expense_id, expenseDTO.getId()))
            throw new IdConflictException("Expense ID in path does not match Expense ID in request body");

        Expense expense = expenseService.update(user_id, budget_id, getExpenseFromDto(expenseDTO));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getExpenseDto(expense));
    }

    @DeleteMapping("/{expense_id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable("user_id") Long user_id,
                                                             @PathVariable("budget_id") Long budget_id,
                                                             @PathVariable("expense_id") Long expense_id) {
        expenseService.deleteById(user_id, budget_id, expense_id);
        String message =
                String.format("Resource /users/%d/budgets/%d/expenses/%d was deleted", user_id, budget_id, expense_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", message));
    }

    private ExpenseDTO getExpenseDto(Expense expense) {
        return modelMapper.map(expense, ExpenseDTO.class);
    }

    private Expense getExpenseFromDto(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, Expense.class);
    }
}
