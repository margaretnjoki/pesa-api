package com.margaretnjoki.pesaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StkCallback(
        @JsonProperty("MerchantRequestID") String merchantRequestId,
        @JsonProperty("CheckoutRequestID") String checkoutRequestId,
        @JsonProperty("ResultCode") int resultCode,
        @JsonProperty("ResultDesc") String resultDesc,
        @JsonProperty("CallbackMetadata") CallbackMetadata callbackMetadata
) {}
