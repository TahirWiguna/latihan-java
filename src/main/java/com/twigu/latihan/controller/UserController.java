package com.twigu.latihan.controller;

import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.helper.ValidationHelper;
import com.twigu.latihan.request.user.UserRegisterRequest;
import com.twigu.latihan.request.user.UserUpdateRequest;
import com.twigu.latihan.response.user.UserRegisterResponse;
import com.twigu.latihan.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<MyRes<List<User>>> getUser() {
        try {
            List<User> data = userService.getUsers();

            for (User user : data) {
                log.info("User data:");

                // Use reflection to get all fields and their values
                Field[] fields = user.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        log.info(field.getName() + ": " + field.get(user));
                    } catch (IllegalAccessException e) {
                        log.error("Error accessing field: " + field.getName(), e);
                    }
                }
            }
            return MyRes.success(data);
        } catch (Exception e) {
            return MyRes.error();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MyRes<User>> me(User user){
        try {
            User data = userService.me(user);
            return MyRes.success(data);
        } catch (ResponseStatusException | ConstraintViolationException e){
            throw e;
        } catch (Exception e) {
            return MyRes.error();
        }
    }
    @PatchMapping("/me")
    public ResponseEntity<MyRes<User>> meUpdate(User user, @RequestBody UserUpdateRequest req){
        try {
            User data = userService.meUpdate(user, req);
            return MyRes.success(data);
        } catch (ResponseStatusException | ConstraintViolationException e){
            throw e;
        } catch (Exception e) {
            return MyRes.error();
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<MyRes<UserRegisterResponse>> createUser(@RequestBody UserRegisterRequest req) {

        try {
            User data = userService.createUser(req);
            UserRegisterResponse newData = new UserRegisterResponse(data);

            return MyRes.created(newData);
        } catch (ResponseStatusException | ConstraintViolationException e){
            throw e;
        } catch (Exception e) {
            return MyRes.error();
        }

    }

    
}
