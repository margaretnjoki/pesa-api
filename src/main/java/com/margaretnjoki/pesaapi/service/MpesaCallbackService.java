package com.margaretnjoki.pesaapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margaretnjoki.pesaapi.dto.CallbackItem;
import com.margaretnjoki.pesaapi.dto.CallbackMetadata;
import com.margaretnjoki.pesaapi.dto.StkCallback;
import com.margaretnjoki.pesaapi.dto.StkCallbackPayload;
import com.margaretnjoki.pesaapi.model.MpesaTransaction;
import com.margaretnjoki.pesaapi.model.TransactionStatus;
import com.margaretnjoki.pesaapi.repository.MpesaTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class MpesaCallbackService {

    private final MpesaTransactionRepository repository;
    private final ObjectMapper objectMapper;

    public MpesaCallbackService(MpesaTransactionRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void process(StkCallbackPayload payload) {
        log.info("processing callback");
        StkCallback callback = payload.body().stkCallback();

        MpesaTransaction transaction = repository.findByCheckoutRequestId(callback.checkoutRequestId())
                .orElseThrow(() -> new IllegalStateException(
                        "No matching transaction for checkoutRequestId=" + callback.checkoutRequestId()));

        transaction.setResultCode(callback.resultCode());
        transaction.setResultDesc(callback.resultDesc());
        transaction.setStatus(callback.resultCode() == 0 ? TransactionStatus.SUCCESS : TransactionStatus.FAILED);

        if (callback.resultCode() == 0 && callback.callbackMetadata() != null) {
            Object receipt = findMetadataValue(callback.callbackMetadata(), "MpesaReceiptNumber");
            if (receipt != null) {
                log.info("receipt {}", receipt);
                transaction.setMpesaReceiptNumber(receipt.toString());
            }
        }

        try {
            transaction.setCallbackPayLoad(objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.error("Failed to serialize callback payload for storage", e);
        }

        transaction.setUpdatedAt(Instant.now());
        repository.save(transaction);

        log.info("Transaction {} updated to status {}", transaction.getId(), transaction.getStatus());
    }

    private Object findMetadataValue(CallbackMetadata metadata, String name) {
        return metadata.item().stream()
                .filter(item -> item.name().equals(name))
                .map(CallbackItem::value)
                .findFirst()
                .orElse(null);
    }
}