package com.twigu.latihan.request.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransferRequest {
    @NotNull
    private Long from;

    @NotNull
    private Long to;

    @NotNull
    @DecimalMin(value = "1")
    private BigDecimal amount;

}
