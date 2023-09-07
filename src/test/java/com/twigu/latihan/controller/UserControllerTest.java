package com.twigu.latihan.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twigu.latihan.entity.User;
import com.twigu.latihan.helper.BCrypt;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.request.user.UserRegisterRequest;
import com.twigu.latihan.request.user.UserUpdateRequest;
import com.twigu.latihan.response.FormValidationErrorResponse;
import com.twigu.latihan.response.user.UserRegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTest {

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
        globalUser.setName("AsepGlobal");
        globalUser.setEmail("asep.global@gmail.com");
        globalUser.setUsername("asep.global");
        globalUser.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        globalUser.setToken(UUID.randomUUID().toString());
        globalUser.setTokenExpiredAt(System.currentTimeMillis() + 3600 * 1000);
        userRepository.save(globalUser);
    }

    @Test
    void createUserSuccess() throws Exception {

        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("Asep");
        req.setEmail("asep@gmail.com");
        req.setUsername("asep");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {

            MyRes<UserRegisterResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Asep", res.getData().getName());
            assertEquals("asep@gmail.com", res.getData().getEmail());
            assertEquals("asep", res.getData().getUsername());

            User user = userRepository.findByUsername(req.getUsername()).orElse(null);
            assertNotNull(user);
            assertTrue(BCrypt.checkpw(req.getPassword(), Objects.requireNonNull(user).getPassword()));
        });
    }

    @Test
    void createUserFormValidation() throws Exception {

        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("");
        req.setEmail("");
        req.setUsername("asep");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnprocessableEntity()
        ).andDo(result -> {

            MyRes<FormValidationErrorResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Validation error", res.getRm());
            assertNotNull(res.getData());
        });
    }

    @Test
    void createUserUsernameExist() throws Exception {

        User user = new User();
        user.setName("Asep");
        user.setEmail("asep@gmail.com");
        user.setUsername("asep");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setCreatedAt(new Date());
        userRepository.save(user);

        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("Budi");
        req.setEmail("budi@gmail.com");
        req.setUsername("asep");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {

            MyRes<FormValidationErrorResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Username already registered", res.getRm());
            assertNull(res.getData());
        });
    }

    @Test
    void createUserEmailExist() throws Exception {

        User user = new User();
        user.setName("Asep");
        user.setEmail("asep@gmail.com");
        user.setUsername("asep");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setCreatedAt(new Date());
        userRepository.save(user);

        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("Budi");
        req.setEmail("asep@gmail.com");
        req.setUsername("budi");
        req.setPassword("rahasia");

        mockMvc.perform(
                post("/api/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {

            MyRes<FormValidationErrorResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("Email already registered", res.getRm());
            assertNull(res.getData());
        });
    }

    @Test
    void getMeSuccess() throws Exception {

        mockMvc.perform(
                get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {

                MyRes<UserRegisterResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                assertEquals(globalUser.getName(), res.getData().getName());
                assertEquals(globalUser.getEmail(), res.getData().getEmail());
                assertEquals(globalUser.getUsername(), res.getData().getUsername());

        });
    }

    @Test
    void getMeNoToken() throws Exception {

        mockMvc.perform(
                get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void getMeTokenExpired() throws Exception {

        User user = new User();
        user.setName("Ujang");
        user.setEmail("ujang@gmail.com");
        user.setUsername("ujang");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiredAt(System.currentTimeMillis() - 3600);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN",user.getToken())
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void updateMeSuccess() throws Exception {

        User req = new User();
        req.setName("asep edited");
        req.setEmail("asep.edited@gmail.com");
        req.setUsername("asep.edited");
        req.setPassword("rahasia");

        mockMvc.perform(
                patch("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                MyRes<UserUpdateRequest> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                assertEquals(req.getName(), res.getData().getName());
                assertEquals(req.getEmail(), res.getData().getEmail());
                assertEquals(req.getUsername(), res.getData().getUsername());
        });
    }

    @Test
    void updateMeNoToken() throws Exception {

        mockMvc.perform(
                patch("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void updateMeTokenExpired() throws Exception {

        User user = new User();
        user.setName("Ujang");
        user.setEmail("ujang@gmail.com");
        user.setUsername("ujang");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken(UUID.randomUUID().toString());
        user.setTokenExpiredAt(System.currentTimeMillis() - 3600);
        userRepository.save(user);

        mockMvc.perform(
                patch("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN",user.getToken())
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

}