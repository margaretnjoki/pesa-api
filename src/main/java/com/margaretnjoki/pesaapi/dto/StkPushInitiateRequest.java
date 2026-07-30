package com.margaretnjoki.pesaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StkPushInitiateRequest(
        @NotBlank
        @Pattern(regexp = "^254[17][0-9]{8}$", message = "phone must be in format 2547XXXXXXXX or 2541XXXXXXXX")
        String phoneNumber,

        @NotBlank String amount,
        @NotBlank String accountRef
) {
}
