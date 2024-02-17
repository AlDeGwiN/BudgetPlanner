package com.aldegwin.budgetplanner.communication.response.error;

import com.aldegwin.budgetplanner.communication.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse implements Response {
    private Error error;
}
