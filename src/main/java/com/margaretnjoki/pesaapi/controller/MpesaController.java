package com.margaretnjoki.pesaapi.controller;

import com.margaretnjoki.pesaapi.dto.StkPushInitiateRequest;
import com.margaretnjoki.pesaapi.dto.StkPushResponse;
import com.margaretnjoki.pesaapi.service.MpesaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mpesa")
public class MpesaController {
    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @PostMapping("/stk-push")
    public StkPushResponse stkPush(@Valid @RequestBody StkPushInitiateRequest request) {
        return mpesaService.initiateStkPush(request.phoneNumber(), request.amount());
    }
}
