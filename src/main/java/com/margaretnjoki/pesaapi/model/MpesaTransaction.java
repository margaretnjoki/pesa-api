package com.margaretnjoki.pesaapi.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mpesa_transactions")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MpesaTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name= "account_reference")
    private String accountReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "merchant_request_id")
    private String merchantRequestId;

    @Column(name= "checkout_request_id", unique = true)
    private String checkoutRequestId;

    @Column(name= "mpesa_receipt_number")
    private String mpesaReceiptNumber;

    @Column(name = "result_code")
    private Integer resultCode;

    @Column(name= "result_desc")
    private String resultDesc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "callback_payload")
    private String callbackPayLoad;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
