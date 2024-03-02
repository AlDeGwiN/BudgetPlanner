package com.aldegwin.budgetplanner.controller;

import com.aldegwin.budgetplanner.communication.dto.IncomeDTO;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.Income;
import com.aldegwin.budgetplanner.service.IncomeService;
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
@RequestMapping("/users/{user_id}/budgets/{budget_id}/incomes")
public class IncomeRestController {
    private final IncomeService incomeService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getAllIncomes(@PathVariable("user_id") Long user_id,
                                                         @PathVariable("budget_id") Long budget_id) {
        List<IncomeDTO> incomes = ((List<Income>) incomeService.findAll(user_id, budget_id)).stream()
                .map(this::getIncomeDto)
                .toList();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(incomes);
    }

    @GetMapping("/{income_id}")
    public ResponseEntity<IncomeDTO> getIncomeById(@PathVariable("user_id") Long user_id,
                                                   @PathVariable("budget_id") Long budget_id,
                                                   @PathVariable("income_id") Long income_id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getIncomeDto(incomeService.findById(user_id, budget_id, income_id)));
    }

    @PostMapping
    public ResponseEntity<IncomeDTO> createIncome(@PathVariable("user_id") Long user_id,
                                                  @PathVariable("budget_id") Long budget_id,
                                                  @RequestBody @Valid IncomeDTO incomeDTO,
                                                  UriComponentsBuilder uriComponentsBuilder) {
        if(incomeDTO.getId() != null)
            throw new IdConflictException("Income ID must be null");

        Income income = incomeService.save(user_id, budget_id, getIncomeFromDto(incomeDTO));
        return ResponseEntity.created(
                uriComponentsBuilder.path("/users/{user_id}/budgets/{budget_id}/incomes/{income_id}")
                        .build(Map.of("user_id", user_id,
                                "budget_id", budget_id,
                                "income_id", income.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(getIncomeDto(income));
    }

    @PutMapping("/{income_id}")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable("user_id") Long user_id,
                                                  @PathVariable("budget_id") Long budget_id,
                                                  @PathVariable("income_id") Long income_id,
                                                  @RequestBody @Valid IncomeDTO incomeDTO) {
        if(!Objects.equals(income_id, incomeDTO.getId()))
            throw new IdConflictException("Income ID in path does not match Income ID in request body");

        Income income = incomeService.update(user_id, budget_id, getIncomeFromDto(incomeDTO));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getIncomeDto(income));
    }

    @DeleteMapping("/{income_id}")
    public ResponseEntity<Map<String, String>> deleteIncome(@PathVariable("user_id") Long user_id,
                                                            @PathVariable("budget_id") Long budget_id,
                                                            @PathVariable("income_id") Long income_id) {
        incomeService.deleteById(user_id, budget_id, income_id);
        String message =
                String.format("Resource /users/%d/budgets/%d/incomes/%d was deleted", user_id, budget_id, income_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", message));
    }

    private IncomeDTO getIncomeDto(Income income) {
        return modelMapper.map(income, IncomeDTO.class);
    }

    private Income getIncomeFromDto(IncomeDTO incomeDTO) {
        return modelMapper.map(incomeDTO, Income.class);
    }
}
