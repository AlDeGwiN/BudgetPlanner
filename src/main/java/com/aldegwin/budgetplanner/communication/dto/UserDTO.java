package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<Budget> budgets;
}
