package com.margaretnjoki.pesaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CallbackItem(
        @JsonProperty("Name") String name,
        @JsonProperty("Value") Object value
) {}
