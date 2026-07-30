package com.margaretnjoki.pesaapi.dto;

import com.margaretnjoki.pesaapi.model.MpesaTransaction;
import com.margaretnjoki.pesaapi.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MpesaTransactionResponse(
        UUID id,
        String phoneNumber,
        BigDecimal amount,
        TransactionStatus status,
        String mpesaReceiptNumber,
        String resultDesc,
        Instant createdAt
) {
    public static MpesaTransactionResponse from(MpesaTransaction t){
     return new MpesaTransactionResponse(t.getId(), t.getPhoneNumber(), t.getAmount(), t.getStatus(), t.getMpesaReceiptNumber(), t.getResultDesc(), t.getCreatedAt());

    }
}
