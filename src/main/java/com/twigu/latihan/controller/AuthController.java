package com.twigu.latihan.controller;

import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.request.auth.AuthLoginRequest;
import com.twigu.latihan.response.auth.AuthLoginResponse;
import com.twigu.latihan.service.AuthService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MyRes<AuthLoginResponse>> login(@RequestBody AuthLoginRequest req) {
        try {
            AuthLoginResponse data = authService.login(req);
            return MyRes.success(data);
        } catch (ResponseStatusException | ConstraintViolationException e){
            throw e;
        } catch (Exception e) {
            return MyRes.error();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MyRes<String>> logout(User user) {
        try {
            authService.logout(user);
            return MyRes.success();
        } catch (ResponseStatusException | ConstraintViolationException e){
            throw e;
        } catch (Exception e) {
            return MyRes.error();
        }
    }
}
