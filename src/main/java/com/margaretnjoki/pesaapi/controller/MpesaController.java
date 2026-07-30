package com.margaretnjoki.pesaapi.controller;

import com.margaretnjoki.pesaapi.dto.MpesaTransactionResponse;
import com.margaretnjoki.pesaapi.dto.StkPushInitiateRequest;
import com.margaretnjoki.pesaapi.dto.StkPushResponse;
import com.margaretnjoki.pesaapi.dto.TokenStatusResponse;
import com.margaretnjoki.pesaapi.model.MpesaTransaction;
import com.margaretnjoki.pesaapi.service.MpesaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/mpesa")
public class MpesaController {
    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    @PostMapping("/stk-push")
    public StkPushResponse stkPush(@Valid @RequestBody StkPushInitiateRequest request) {
        return mpesaService.initiateStkPush(request.phoneNumber(), request.amount(), request.accountRef());
    }

    @GetMapping("/token-status")
    public TokenStatusResponse tokenStatus(){
        return mpesaService.tokenstatus();
    }

    @GetMapping("/transactions/{id}/status")
    public MpesaTransactionResponse status(@PathVariable UUID id){
        return MpesaTransactionResponse.from(mpesaService.findTransactionById(id));
    }

    @GetMapping("/allTransactions")
    public List<MpesaTransactionResponse> list() {
        return mpesaService.findAll().stream().map(MpesaTransactionResponse::from).toList();
    }

    @GetMapping("/transactions")
    public List<MpesaTransactionResponse> listTransactions(@RequestParam String phone){
        return mpesaService.findByPhoneNumberOrderByCreatedAtDesc(phone);
    }


}
