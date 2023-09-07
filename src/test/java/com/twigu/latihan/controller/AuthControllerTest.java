package com.twigu.latihan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.BCrypt;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.request.auth.AuthLoginRequest;
import com.twigu.latihan.request.user.UserRegisterRequest;
import com.twigu.latihan.response.auth.AuthLoginResponse;
import com.twigu.latihan.response.user.UserRegisterResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User globalUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        globalUser = new User();
        globalUser.setName("Asep");
        globalUser.setEmail("asep@gmail.com");
        globalUser.setUsername("asep");
        globalUser.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        globalUser.setToken(UUID.randomUUID().toString());
        globalUser.setTokenExpiredAt(System.currentTimeMillis() + 3600 * 1000);
        userRepository.save(globalUser);
    }

    @Test
    void loginSuccess() throws Exception {

        AuthLoginRequest req = new AuthLoginRequest();
        req.setUsername("asep");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            MyRes<AuthLoginResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(res.getData().getToken());
            assertNotNull(res.getData().getExpiredAt());

            User user = userRepository.findByUsername(req.getUsername()).orElse(null);
            assertNotNull(user);

            assertEquals(user.getToken(), res.getData().getToken());
            assertEquals(user.getTokenExpiredAt(), res.getData().getExpiredAt());
        });
    }

    @Test
    void loginWrongUsername() throws Exception {

        AuthLoginRequest req = new AuthLoginRequest();
        req.setUsername("asepSalah");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            MyRes<AuthLoginResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(res.getRm(),"Username or password is wrong");
        });
    }

    @Test
    void loginWrongPassword() throws Exception {

        AuthLoginRequest req = new AuthLoginRequest();
        req.setUsername("asep");
        req.setPassword("rahasiaSalah");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            MyRes<AuthLoginResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(res.getRm(),"Username or password is wrong");
        });
    }

    @Test
    void logoutSuccess() throws Exception {

        log.info("masukkkkkkk");
        log.info(globalUser.getUsername());

        mockMvc.perform(
                post("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", globalUser.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            MyRes<String> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            log.info("res: {}", res);
            assertEquals(res.getRm(),"Success");

            User user = userRepository.findByUsername(globalUser.getUsername()).orElse(null);
            assertNotNull(user);

            assertNull(user.getToken());
            assertNull(user.getTokenExpiredAt());
        });
    }

    @Test
    void logoutNoToken() throws Exception {
        mockMvc.perform(
                post("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void logoutInvalidToken() throws Exception {

        mockMvc.perform(
                post("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "tokenSalah")
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

}