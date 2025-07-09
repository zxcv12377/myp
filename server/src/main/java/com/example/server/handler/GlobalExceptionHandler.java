package com.example.server.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handlerBadArg(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", ex.getMessage()));
    }
}
