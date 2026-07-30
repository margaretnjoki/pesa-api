package com.margaretnjoki.pesaapi.controller;

import com.margaretnjoki.pesaapi.dto.StkCallbackPayload;
import com.margaretnjoki.pesaapi.service.MpesaCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mpesa")
public class MpesaCallbackController {
    private final MpesaCallbackService callbackService;


    public MpesaCallbackController(MpesaCallbackService callbackService) {
        this.callbackService = callbackService;
    }
    @PostMapping("/callback")
    public Map<String, Object> receiveCallback(@RequestBody StkCallbackPayload payload){
        log.info("callback received");
        try{
            callbackService.process(payload);
        } catch (Exception e) {
            log.error("Error processing M-Pesa callback", e);
        }
        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }
}
