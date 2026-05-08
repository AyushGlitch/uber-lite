package com.uber.lite.riderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.lite.common.event.RideRequestEventDTO;
import com.uber.lite.common.request.RideRequestDTO;
import com.uber.lite.riderservice.dto.RiderRequestDTO;
import com.uber.lite.riderservice.dto.RiderResponseDTO;
import com.uber.lite.riderservice.mapper.RiderMapper;
import com.uber.lite.riderservice.model.OutboxEvent;
import com.uber.lite.riderservice.model.OutboxStatus;
import com.uber.lite.riderservice.model.Ride;
import com.uber.lite.riderservice.model.Rider;
import com.uber.lite.riderservice.repository.OutboxEventRepository;
import com.uber.lite.riderservice.repository.RideRepository;
import com.uber.lite.riderservice.repository.RiderRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RiderService {
    private final RiderRepository riderRepository;
    private final RiderMapper riderMapper;
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public RiderService (RiderRepository riderRepository, RiderMapper riderMapper,
                         RideRepository rideRepository, KafkaTemplate<String, Object> kafkaTemplate
                        , OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.riderRepository = riderRepository;
        this.riderMapper = riderMapper;
        this.rideRepository = rideRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public RiderResponseDTO registerRider(RiderRequestDTO riderRequestDTO) {
        riderRepository.findByEmail(riderRequestDTO.email()).ifPresent(
                rider -> {
                    throw new RuntimeException("Rider with email " + riderRequestDTO.email() + " already exists");
                }
        );

        Rider rider = riderMapper.toModal(riderRequestDTO);
        Rider savedRider = riderRepository.save(rider);

        return riderMapper.toDto(savedRider);
    }

    public RiderResponseDTO getRiderById(UUID id) {
        Rider rider = riderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rider with id " + id + " not found")
        );

        return riderMapper.toDto(rider);
    }

    @Transactional
    public void requestRide(RideRequestDTO dto) {
        if (!riderRepository.existsById(dto.riderId())) {
            throw new RuntimeException("Rider with id " + dto.riderId() + " not found");
        }

        Ride ride = Ride.builder()
                .riderId(dto.riderId())
                .pickupLat(dto.pickupLat())
                .pickupLon(dto.pickupLon())
                .dropoffLat(dto.dropoffLat())
                .dropoffLon(dto.dropoffLon())
                .status("REQUESTED")
                .createdAt(LocalDateTime.now())
                .build();

        Ride savedRide = rideRepository.save(ride);

        RideRequestEventDTO event = new RideRequestEventDTO(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLat(),
                savedRide.getPickupLon(),
                savedRide.getDropoffLat(),
                savedRide.getDropoffLon()
        );

        try {
            outboxEventRepository.save(OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("RIDE")
                    .aggregateId(savedRide.getId())
                    .eventType("RideRequestEventDTO")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build()
            );
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ride request event", e);
        }
    }
}
