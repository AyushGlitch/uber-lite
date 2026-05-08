package com.uber.lite.tripservice.kafka;

import com.uber.lite.common.event.MatchProposedEventDTO;
import com.uber.lite.tripservice.model.Trip;
import com.uber.lite.tripservice.model.TripStatus;
import com.uber.lite.tripservice.repository.TripRepository;
import com.uber.lite.tripservice.service.TripStateMachineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchProposedConsumer {
    private final TripRepository tripRepository;
    private final TripStateMachineService stateMachineService;

    @KafkaListener(topics = "${app.kafka.topics.match-proposals}", groupId = "trip-service-group")
    @Transactional
    public void consume(@Nullable MatchProposedEventDTO event) {
        if (event == null) {
            log.warn("Skipping malformed match proposed message (deserialization failed)");
            return;
        }

        Trip trip = tripRepository.findByRideId(event.rideId()).orElseGet(() ->
                Trip.builder()
                        .id(UUID.randomUUID())
                        .rideId(event.rideId())
                        .riderId(event.riderId())
                        .status(TripStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        stateMachineService.assertTransition(trip.getStatus(), TripStatus.MATCHED);
        trip.setDriverId(event.driverId());
        trip.setStatus(TripStatus.MATCHED);
        trip.setUpdatedAt(LocalDateTime.now());
        tripRepository.save(trip);

        log.info("Trip {} transitioned to MATCHED for ride {}", trip.getId(), event.rideId());
    }
}
