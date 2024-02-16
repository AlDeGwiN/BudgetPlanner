package com.aldegwin.budgetplanner.communication.response.success;

import com.aldegwin.budgetplanner.communication.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SuccessResponse <T> implements Response {
    private T data;
}
