package com.uber.lite.driverservice.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String customMessageCode,
        String message,
        LocalDateTime timestamp
) {}
