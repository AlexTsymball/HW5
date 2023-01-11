package com.example.hw5.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        HashMap<String, Object> resObj = new HashMap<>();
        String errorMsg = "validation is failed!";
        if (ex.getErrorCount() > 0) {
            List<String> errorDetails = new ArrayList<>();
            for (ObjectError error : ex.getBindingResult().getAllErrors()) {
                errorDetails.add(error.getDefaultMessage());
            }

            if (errorDetails.size() > 0) errorMsg = errorDetails.get(0);
        }

        resObj.put("status", HttpStatus.BAD_REQUEST);
        resObj.put("message", errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObj);

    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
//        log.warn("IllegalArgumentException thrown: {}", e.getMessage());
//        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
//    }
//
//    private static ResponseEntity<Object> buildResponse(
//            HttpStatus httpStatus, String message) {
//        ErrorResponse response = new ErrorResponse(httpStatus.value(),
//                httpStatus.getReasonPhrase(), message);
//        return ResponseEntity.status(httpStatus.value()).body(response);
//    }
//
//    @Getter
//    @AllArgsConstructor
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    static class ErrorResponse {
//        private int status;
//        private String error;
//        private String message;
//    }
}

