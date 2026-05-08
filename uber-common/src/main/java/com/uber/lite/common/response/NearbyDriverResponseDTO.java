package com.uber.lite.common.response;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NearbyDriverResponseDTO(
        @NotNull UUID driverId,
        @NotNull double lat,
        @NotNull double lon
) {}
