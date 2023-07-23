package ru.practicum.shareit.exception;

import lombok.Data;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Generated
public class ExceptHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestException(BadRequestException badRequestException) {
        return new ErrorResponse(badRequestException.getMessage());
    }

    @Data
    @RequiredArgsConstructor
    private static class ErrorResponse {
        private final String error;
        private String description;
    }
}
