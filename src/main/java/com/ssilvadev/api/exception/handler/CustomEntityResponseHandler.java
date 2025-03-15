package com.ssilvadev.api.exception.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ssilvadev.api.exception.ExceptionResponse;
import com.ssilvadev.api.exception.ResourceNotFoundException;
import com.ssilvadev.api.exception.UnsuportedMathOperationException;

@ControllerAdvice
@RestController
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handlerAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsuportedMathOperationException.class)
    public final ResponseEntity<ExceptionResponse> handlerBadRequestException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> resourceNotFoundException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
