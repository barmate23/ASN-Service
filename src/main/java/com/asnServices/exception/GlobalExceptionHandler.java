package com.asnServices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<Map<String,Object>> validationFailureException(ValidationFailureException failureException){

        Map<String,Object> map=new HashMap<>();
        map.put("message",failureException.getMessage());
        map.put("success",false);
        map.put("status", HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(map);
    }

}
