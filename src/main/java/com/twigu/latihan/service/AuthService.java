package com.twigu.latihan.service;

import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.BCrypt;
import com.twigu.latihan.helper.ValidationHelper;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.request.auth.AuthLoginRequest;
import com.twigu.latihan.response.auth.AuthLoginResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationHelper validationHelper;

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest req) {
        validationHelper.validate(req);

        Optional<User> userOptional = userRepository.findByUsername(req.getUsername());

        if(userOptional.isEmpty() || !BCrypt.checkpw(req.getPassword(), userOptional.get().getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password is wrong");
        }

        User user = userOptional.get();
        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiredAt(nextWeek());
        userRepository.save(user);

        AuthLoginResponse res = new AuthLoginResponse();
        res.setToken(user.getToken());
        res.setExpiredAt(user.getTokenExpiredAt());

        return res;
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }

    private Long nextWeek() {
        int second = 1000;
        int minute = 60 * second;
        int hour = 60 * minute;
        int day = 24 * hour;
        int totalDay = 7;
        return System.currentTimeMillis() + ((long) totalDay * day);
    }
}
