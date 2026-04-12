package com.uber.lite.riderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID riderId;
    private double pickupLat;
    private double pickupLon;
    private double dropoffLat;
    private double dropoffLon;

    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
