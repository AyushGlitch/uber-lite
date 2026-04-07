package com.uber.lite.riderservice.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String customErrorCode,
        String message,
        LocalDateTime timestamp
) {}
