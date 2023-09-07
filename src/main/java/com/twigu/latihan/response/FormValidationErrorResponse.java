package com.twigu.latihan.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormValidationErrorResponse {
    private final String rc = "99";
    private final String rm = "Validation error";
    private Object[] data;
}
