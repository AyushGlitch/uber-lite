package com.uber.lite.common.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RiderResponseDTO(
   UUID id,
   String name,
   String email,
   LocalDateTime createdAt
) {}
