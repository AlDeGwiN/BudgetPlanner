package com.aldegwin.budgetplanner.controller;


import com.aldegwin.budgetplanner.communication.dto.BudgetDTO;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.Budget;
import com.aldegwin.budgetplanner.service.BudgetService;
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
@RequestMapping("/users/{user_id}/budgets")
@RequiredArgsConstructor
public class BudgetRestController {
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getAllBudgets(@PathVariable("user_id") Long user_id) {
        List<BudgetDTO> budgets =
                ((List<Budget>) budgetService.findAll(user_id)).stream()
                        .map(this::getBudgetDto)
                        .toList();
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(budgets);
    }

    @GetMapping("/{budget_id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable("user_id") Long user_id,
                                                  @PathVariable("budget_id") Long budget_id) {
        BudgetDTO budgetDTO = getBudgetDto(budgetService.findById(user_id, budget_id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(budgetDTO);
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody @Valid BudgetDTO budgetDTO,
                                                 @PathVariable("user_id") Long user_id,
                                                 UriComponentsBuilder uriComponentsBuilder) {
        if(budgetDTO.getId() != null)
            throw new IdConflictException("Budget ID must be null");

        Budget budget = budgetService.save(user_id, getBudgetFromDto(budgetDTO));

        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/users/{user_id}/budgets/{budget_id}")
                        .build(Map.of("user_id", user_id, "budget_id", budget.getId())))
                .body(getBudgetDto(budget));
    }

    @PutMapping("/{budget_id}")
    public ResponseEntity<BudgetDTO> updateBudget(@RequestBody @Valid BudgetDTO budgetDTO,
                                                 @PathVariable("user_id") Long user_id,
                                                 @PathVariable("budget_id") Long budget_id) {

        if(!Objects.equals(budget_id, budgetDTO.getId()))
            throw new IdConflictException("Budget ID in path does not match Budget ID in request body");

        Budget budget = budgetService.update(user_id, getBudgetFromDto(budgetDTO));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getBudgetDto(budget));
    }

    @DeleteMapping("/{budget_id}")
    public ResponseEntity<Map<String, String>> deleteBudget(@PathVariable("user_id") Long user_id,
                                                            @PathVariable("budget_id") Long budget_id) {
        budgetService.deleteById(user_id, budget_id);
        String message = String.format("Resource /users/%d/budgets/%d was deleted", user_id, budget_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", message));
    }

    private BudgetDTO getBudgetDto(Budget budget) {
        return modelMapper.map(budget, BudgetDTO.class);
    }

    private Budget getBudgetFromDto(BudgetDTO budgetDTO) {
        return modelMapper.map(budgetDTO, Budget.class);
    }
}
