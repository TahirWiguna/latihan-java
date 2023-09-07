package com.twigu.latihan.service;

import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.BCrypt;
import com.twigu.latihan.helper.ValidationHelper;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.request.auth.AuthLoginRequest;
import com.twigu.latihan.request.user.UserRegisterRequest;
import com.twigu.latihan.request.user.UserUpdateRequest;
import com.twigu.latihan.response.auth.AuthLoginResponse;
import com.twigu.latihan.response.user.UserRegisterResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationHelper validationHelper;

    public User createUser(UserRegisterRequest req) {
        validationHelper.validate(req);


        if(userRepository.existsByEmail(req.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        if(userRepository.existsByUsername(req.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User me(User user) {
        return user;
    }

    public User meUpdate(User user, UserUpdateRequest req) {
        validationHelper.validate(req);

        if(userRepository.existsByEmail(req.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        if(userRepository.existsByUsername(req.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        if (Objects.nonNull(req.getName())) {
            user.setName(req.getName());
        }

        if (Objects.nonNull(req.getEmail())) {
            user.setEmail(req.getEmail());
        }

        if (Objects.nonNull(req.getUsername())) {
            user.setUsername(req.getUsername());
        }

        if (Objects.nonNull(req.getPassword())) {
            user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
        }

        user.setUpdatedAt(new Date());
        user.setUpdatedBy(user.getId());

        return userRepository.save(user);
    }


}
