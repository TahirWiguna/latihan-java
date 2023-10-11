package com.twigu.latihan.request.wallet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class WalletDepositRequest {

    @JsonIgnore
    @NotNull
    private Long idWallet;

    @DecimalMin(value = "1")
    @NotNull
    private BigDecimal amount;

}
