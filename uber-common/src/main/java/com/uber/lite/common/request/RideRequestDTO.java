package com.uber.lite.common.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RideRequestDTO(
        @NotNull UUID riderId,
        @NotNull Double pickupLat,
        @NotNull Double pickupLon,
        @NotNull Double dropoffLat,
        @NotNull Double dropoffLon
) {}
