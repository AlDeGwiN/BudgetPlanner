package com.aldegwin.budgetplanner.exception;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class NotUniqueFieldException extends RuntimeException{
    List<String> messages;

    public NotUniqueFieldException(List<String> messages) {
        this.messages = messages;
    }
}
