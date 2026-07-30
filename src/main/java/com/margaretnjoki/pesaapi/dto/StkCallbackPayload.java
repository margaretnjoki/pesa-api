package com.margaretnjoki.pesaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StkCallbackPayload(@JsonProperty("Body") CallbackBody body) {}

