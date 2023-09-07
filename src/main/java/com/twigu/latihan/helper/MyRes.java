package com.twigu.latihan.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
@Builder
public class MyRes<T> {
    private String rc;
    private String rm;
    private T data;

    public MyRes(String rc, String rm, T data){
        this.rc = rc;
        this.rm = rm;
        this.data = data;
    }

    public static <T> ResponseEntity<MyRes<T>> success() {
        MyRes<T> response = new MyRes<>("00", "Success", null);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    public static <T> ResponseEntity<MyRes<T>> success(T data) {
        MyRes<T> response = new MyRes<>("00", "Success", data);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public static <T> ResponseEntity<MyRes<T>> success(T data, String rm) {
        MyRes<T> response = new MyRes<>("00", rm, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<MyRes<T>> created(T data) {
        MyRes<T> response = new MyRes<>("00", "Data has been created!", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    public static <T> ResponseEntity<MyRes<T>> created(T data, String rm) {
        MyRes<T> response = new MyRes<>("00", rm, data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<MyRes<T>> badRequest() {
        MyRes<T> response = new MyRes<>("99", "Oops! Something went wrong. Please try again later.", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<MyRes<T>> badRequest(String rc) {
        MyRes<T> response = new MyRes<>(rc, "Oops! Something went wrong. Please try again later.", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    public static <T> ResponseEntity<MyRes<T>> badRequest(String rc, String rm) {
        MyRes<T> response = new MyRes<>(rc, rm, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    public static <T> ResponseEntity<MyRes<T>> notFound() {
        MyRes<T> response = new MyRes<>("00", "Not Found.", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<MyRes<T>> notFound(String rm) {
        MyRes<T> response = new MyRes<>("00", rm, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<MyRes<T>> error(String rm, T data) {
        MyRes<T> response = new MyRes<>("99", rm, data);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<MyRes<T>> error(T data) {
        MyRes<T> response = new MyRes<>("99", "Oops! Something went wrong. Please try again later.", data);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<MyRes<T>> error() {
        MyRes<T> response = new MyRes<>("99", "Oops! Something went wrong. Please try again later.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }




}

