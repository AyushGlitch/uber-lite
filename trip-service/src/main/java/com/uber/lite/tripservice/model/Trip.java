package com.uber.lite.tripservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    private UUID id;

    private UUID rideId;
    private UUID riderId;
    private UUID driverId;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
