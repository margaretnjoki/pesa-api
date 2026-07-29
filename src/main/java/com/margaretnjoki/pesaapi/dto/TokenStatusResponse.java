package com.margaretnjoki.pesaapi.dto;

import java.time.Instant;

public record TokenStatusResponse(
        boolean cached,
        Instant expiresAt
) {}
