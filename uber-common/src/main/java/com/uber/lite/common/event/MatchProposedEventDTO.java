package com.uber.lite.common.event;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MatchProposedEventDTO (
        @NotNull UUID rideId,
        @NotNull UUID riderId,
        @NotNull UUID driverId,
        @NotNull double driverLat,
        @NotNull double driverLon
) {}
