package com.uber.lite.driverservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Driver name cannot be null")
    private String name;

    @NotNull(message = "License plate cannot be null")
    @Column(unique = true)
    private String licensePlate;

    @NotNull(message = "Vehicle type cannot be null")
    private String vehicleType;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}
