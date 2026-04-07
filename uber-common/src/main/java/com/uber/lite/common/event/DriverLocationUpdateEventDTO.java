package com.uber.lite.common.event;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record DriverLocationUpdateEventDTO (
        @NotNull UUID driverId,
        @NotNull Double lat,
        @NotNull Double lon,
        @NotNull Instant timestamp
) {}
