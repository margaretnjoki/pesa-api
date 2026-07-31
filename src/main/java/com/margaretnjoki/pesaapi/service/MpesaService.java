package com.margaretnjoki.pesaapi.service;

import com.margaretnjoki.pesaapi.dto.*;
import com.margaretnjoki.pesaapi.model.MpesaTransaction;
import com.margaretnjoki.pesaapi.model.TransactionStatus;
import com.margaretnjoki.pesaapi.mpesa.MpesaProperties;
import com.margaretnjoki.pesaapi.repository.MpesaTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MpesaService {
    private final RestClient restClient;
    private final MpesaProperties properties;
    private final MpesaTransactionRepository mpesaTransactionRepository;

    private String cachedToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public MpesaService(RestClient restClient, MpesaProperties properties, MpesaTransactionRepository mpesaTransactionRepository) {
        this.restClient = restClient;
        this.properties = properties;
        this.mpesaTransactionRepository = mpesaTransactionRepository;
    }

    public String getAccessToken() {
        log.info("Consumer Key: {}", properties.getConsumerKey());
        log.info("Consumer Secret: {}", properties.getConsumerSecret());
        log.info("Base URL: {}", properties.getBaseUrl());

        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return cachedToken;
        }

        String credentials = properties.getConsumerKey() + ":" + properties.getConsumerSecret();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        MpesaAuthResponse response = restClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .retrieve()
                .body(MpesaAuthResponse.class);

        cachedToken = response.accessToken();
        long expiresInSeconds = Long.parseLong(response.expiresIn());
        tokenExpiresAt = Instant.now().plusSeconds(expiresInSeconds - 60);   // refresh a bit early, not exactly at the edge

        log.info("Fetched a new M-Pesa access token, valid ~{} seconds", expiresInSeconds);
        return cachedToken;
    }

    public StkPushResponse initiateStkPush(String phoneNumber, String amount, String accountRef) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneId.of("Africa/Nairobi"))
                .format(Instant.now());
        String rawPassword = properties.getShortcode() + properties.getPasskey() + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
        StkPushRequest request = new StkPushRequest(
                properties.getShortcode(),
                password,
                timestamp,
                "CustomerPayBillOnline",
                amount,
                phoneNumber,
                properties.getShortcode(),
                phoneNumber,
                properties.getCallbackUrl(),
                "PesaApi",
                "Payment via pesa-api"
        );

        log.info("Initiating STK push: phone={}, amount={}", phoneNumber, amount);

        StkPushResponse response = restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .body(request)
                .retrieve()
                .body(StkPushResponse.class);

        MpesaTransaction transaction = MpesaTransaction.builder()
                .phoneNumber(phoneNumber)
                .amount(new BigDecimal(amount))
                .accountReference(accountRef)
                .status(TransactionStatus.PENDING)
                .merchantRequestId(response.merchantRequestId())
                .checkoutRequestId(response.checkoutRequestId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        mpesaTransactionRepository.save(transaction);
        return response;
    }

    public TokenStatusResponse tokenstatus() {
        boolean isCached = cachedToken != null && Instant.now().isBefore(tokenExpiresAt);
        return new TokenStatusResponse(
                isCached,
                isCached ? tokenExpiresAt : null
        );
    }

    public MpesaTransaction findTransactionById(UUID id) {
        return mpesaTransactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<MpesaTransaction> findAll() {
        log.info("get all transactions");
        return mpesaTransactionRepository.findAll();

    }

    public List<MpesaTransactionResponse> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber) {
        log.info("finding all the transactions for phoneNumber: {}", phoneNumber);
        return mpesaTransactionRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .stream()
                .map(MpesaTransactionResponse::from)
                .toList();
    }
}
