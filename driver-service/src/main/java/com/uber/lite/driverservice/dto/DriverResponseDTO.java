package com.uber.lite.driverservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DriverResponseDTO(
        UUID id,
        String name,
        String licensePlate,
        String vehicleType,
        String Status,
        LocalDateTime createdAt
) {}
