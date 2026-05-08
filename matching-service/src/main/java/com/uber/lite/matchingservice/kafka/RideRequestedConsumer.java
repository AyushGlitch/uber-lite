package com.uber.lite.matchingservice.kafka;

import com.uber.lite.common.event.MatchProposedEventDTO;
import com.uber.lite.common.event.RideRequestEventDTO;
import com.uber.lite.matchingservice.client.LocationServiceClient;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RideRequestedConsumer {
    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics="${app.kafka.topics.rideRequests}", groupId = "matching-service-group")
    public void consume(@Nullable RideRequestEventDTO event) {
        if (event == null) {
            return;
        }

        List<UUID> nearbyDrivers = locationServiceClient.nearbyDrivers(event.pickupLat(), event.pickupLon(), 5.0);
        if (nearbyDrivers.isEmpty()) {
            log.info("No nearby drivers found for ride request {}", event.rideId());
            return;
        }

        UUID selectedDriver = nearbyDrivers.get(0);
        MatchProposedEventDTO proposed = new MatchProposedEventDTO(
                event.rideId(),
                event.riderId(),
                selectedDriver,
                0.0,
                0.0
        );

        kafkaTemplate.send("${app.kafka.topics.matchProposals}", event.rideId().toString(), proposed);
        log.info("Proposed match for ride {}: driver {}", event.rideId(), selectedDriver);
    }
}
