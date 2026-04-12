package com.uber.lite.common.request;

import jakarta.validation.constraints.NotBlank;

public record DriverRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "License plate is required") String licensePlate,
        @NotBlank(message = "Vehicle type is required") String vehicleType
) {}
