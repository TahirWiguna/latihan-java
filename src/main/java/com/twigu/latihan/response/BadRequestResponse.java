package com.twigu.latihan.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadRequestResponse {
    private final String rc = "99";
    private String rm = "Validation error";
    private final String data = null;
}
