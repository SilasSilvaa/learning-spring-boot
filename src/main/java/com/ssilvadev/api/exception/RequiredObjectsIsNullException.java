package com.ssilvadev.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectsIsNullException extends RuntimeException {
    public RequiredObjectsIsNullException(String message) {
        super(message);
    }

    public RequiredObjectsIsNullException() {
        super("It is not allowed to persist a null object!");
    }
}
