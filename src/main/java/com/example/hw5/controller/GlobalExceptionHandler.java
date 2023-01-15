package com.example.hw5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, Object> resObj = new HashMap<>();
        StringBuilder errorMsg = new StringBuilder("validation is failed!");
        if (ex.getErrorCount() > 0) {
            for (ObjectError error : ex.getBindingResult().getAllErrors()) {
                errorMsg.append("\n ").append(error.getDefaultMessage());
            }
        }
        resObj.put("status", HttpStatus.BAD_REQUEST);
        resObj.put("message", errorMsg.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObj);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(ConstraintViolationException ex) {
        HashMap<String, Object> resObj = new HashMap<>();
        StringBuilder errorMsg = new StringBuilder("validation is failed! ");
        if (ex.getConstraintViolations() != null) {
            for (ConstraintViolation currentViolation : ex.getConstraintViolations()) {
                errorMsg.append("\n ").append(currentViolation.getMessageTemplate());
            }
        }
        resObj.put("status", HttpStatus.BAD_REQUEST);
        resObj.put("message", errorMsg.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObj);
    }
}

