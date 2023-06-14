package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundExceptionHandler(NotFoundException notFoundException) {
        return notFoundException.getMessage();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validationExceptionHandler(ValidationException validationException) {
        return validationException.getMessage();
    }

    @ExceptionHandler(EmailValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String emailValidationException(EmailValidationException emailValidationException) {
        return emailValidationException.getMessage();
    }
}
