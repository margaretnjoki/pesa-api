package com.margaretnjoki.pesaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CallbackBody(@JsonProperty("stkCallback") StkCallback stkCallback) {}
