package com.aldegwin.budgetplanner.communication.response.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ValidErrorResponse {
    private List<Error> errors;
}
