package com.lpp.transaction.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

/**
 * @author ：liupanpan
 */
@RestControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ ApplicationException.class })
    public ResponseEntity<Object> handleException(ApplicationException e) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST, e.getErrorCode(), e.getErrorMessage(), e),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("handleMethodArgumentNotValid", ex);
        StringBuilder messages = new StringBuilder();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        globalErrors.forEach(error -> messages.append(error.getDefaultMessage()).append(";"));
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(
                error -> messages.append(error.getField()).append(" ").append(error.getDefaultMessage()).append(";"));
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.name(), messages.toString(), ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ UndeclaredThrowableException.class })
    public ResponseEntity<Object> handleException(UndeclaredThrowableException e) {
        log.error("handleException", e);
        if (e.getUndeclaredThrowable().getCause() instanceof ApplicationException) {
            ApplicationException ex = (ApplicationException) e.getUndeclaredThrowable().getCause();
            return new ResponseEntity<>(
                    new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getErrorMessage(), ex),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                new ApiError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name(), e.getMessage(), e),
                HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
