package com.margaretnjoki.pesaapi.controller;

import com.margaretnjoki.pesaapi.dto.StkPushInitiateRequest;
import com.margaretnjoki.pesaapi.dto.StkPushResponse;
import com.margaretnjoki.pesaapi.dto.TokenStatusResponse;
import com.margaretnjoki.pesaapi.service.MpesaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/token-status")
    public TokenStatusResponse tokenStatus(){
        return mpesaService.tokenstatus();
    }
}
