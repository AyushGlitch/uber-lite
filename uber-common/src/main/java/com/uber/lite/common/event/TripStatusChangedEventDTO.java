package com.uber.lite.common.event;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record TripStatusChangedEventDTO (
        @NotNull UUID tripId,
        @NotNull String oldStatus,
        @NotNull String newStatus,
        @NotNull Instant occuredAt
) {}
