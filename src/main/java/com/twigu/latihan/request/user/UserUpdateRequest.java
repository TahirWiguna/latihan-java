package com.twigu.latihan.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 50)
    private String email;

    @Size(min = 3, max = 20)
    private String username;

    @Size(min = 6, max = 40)
    private String password;
}
