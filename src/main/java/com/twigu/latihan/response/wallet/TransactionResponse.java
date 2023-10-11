package com.twigu.latihan.response.wallet;

import com.twigu.latihan.enums.TrxType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private String from;
    private String to;
    private TrxType transactionType;
    private BigDecimal amount;
    private Date date;
}
