package com.aldegwin.budgetplanner.communication.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotNull(message = "Username cannot be empty")
    @Size(min = 4, max = 8,
            message = "The length of the username must be from 4 characters to 8")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "The username can only consist of Latin letters and numbers")
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, max = 20, message = "The length of the password must be from 8 characters to 20")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "The password can only consist of Latin letters and numbers")
    private String password;

    @NotNull(message = "The mail cannot be empty")
    @Email(message = "Must have the format of an email address")
    private String email;
}
