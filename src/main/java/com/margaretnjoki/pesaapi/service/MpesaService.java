package com.margaretnjoki.pesaapi.service;

import com.margaretnjoki.pesaapi.dto.MpesaAuthResponse;
import com.margaretnjoki.pesaapi.dto.StkPushRequest;
import com.margaretnjoki.pesaapi.dto.StkPushResponse;
import com.margaretnjoki.pesaapi.mpesa.MpesaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
@Service
@Slf4j
public class MpesaService {
    private final RestClient restClient;
    private final MpesaProperties properties;

    private String cachedToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public MpesaService(RestClient restClient, MpesaProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
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

    public StkPushResponse initiateStkPush(String phoneNumber, String amount){
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneId.of("Africa/Nairobi"))
                .format(Instant.now());
        String rawPassword = properties.getShortcode() + properties.getPasskey() + timestamp ;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
        log.info("Timestamp: {}", timestamp);
        StkPushRequest request= new StkPushRequest(
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

        return restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .body(request)
                .retrieve()
                .body(StkPushResponse.class);

    }
}
