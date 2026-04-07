package com.uber.lite.common.event;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;


public record RideRequestEventDTO(
    @NotNull UUID riderId,
    @NotNull Double pickupLat,
    @NotNull Double pickupLon,
    @NotNull Double dropoffLat,
    @NotNull Double dropoffLon
) {}