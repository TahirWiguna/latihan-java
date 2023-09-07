package com.twigu.latihan.controller;

import com.twigu.latihan.helper.MyRes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MyRes<Map<String, List<String>>>> handleConstraintViolationException(ConstraintViolationException ex) {
        // Collect all validation error messages
        Map<String, List<String>> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));

        // Customize the response
        MyRes<Map<String, List<String>>> response = MyRes.<Map<String, List<String>>>builder()
                .data(errors)
                .rm("Validation error")
                .rc("99")
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MyRes<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(MyRes.<String>builder()
                        .data(null)
                        .rc("99")
                        .rm(exception.getReason())
                        .build());
    }
}
