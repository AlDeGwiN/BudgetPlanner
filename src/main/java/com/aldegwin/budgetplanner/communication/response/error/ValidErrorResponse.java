package com.aldegwin.budgetplanner.communication.response.error;

import com.aldegwin.budgetplanner.communication.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ValidErrorResponse implements Response {
    private List<Error> errors;
}
