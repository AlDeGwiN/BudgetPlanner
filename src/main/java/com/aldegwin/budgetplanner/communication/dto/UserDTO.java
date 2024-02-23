package com.aldegwin.budgetplanner.communication.dto;

import com.aldegwin.budgetplanner.model.Budget;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    @NotNull(message = "Username cannot be empty")
    @Size(min = 4, max = 8,
            message = "The length of the username must be from 4 characters to 8")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "The username can only consist of Latin letters and numbers")
    private String username;
    @NotNull(message = "The mail cannot be empty")
    @Email(message = "Must have the format of an email address")
    private String email;
    private List<Budget> budgets;
}
