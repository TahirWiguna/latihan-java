package com.twigu.latihan.entity;

import com.twigu.latihan.enums.TrxType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Wallet senderWallet;

    @ManyToOne
    private Wallet receiverWallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrxType transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne
    private User createdBy;
}
