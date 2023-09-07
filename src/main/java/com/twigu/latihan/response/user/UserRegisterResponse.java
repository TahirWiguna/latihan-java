package com.twigu.latihan.response.user;

import com.twigu.latihan.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterResponse {
    private String name;
    private String email;
    private String username;
    private Date createdAt;

    public UserRegisterResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
    }
}
