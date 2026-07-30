package com.margaretnjoki.pesaapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CallbackMetadata(@JsonProperty("Item") List<CallbackItem> item) {}
