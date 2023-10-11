package com.twigu.latihan.request.wallet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletWithdrawRequest {

    @JsonIgnore
    @NotNull
    private Long id;

    @DecimalMin(value = "1")
    private BigDecimal amount;

}
