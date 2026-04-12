package com.uber.lite.locationservice.kafka;

import com.uber.lite.common.event.DriverLocationUpdateEventDTO;
import com.uber.lite.locationservice.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DriverLocationConsumer {
    private final LocationService locationService;

    public DriverLocationConsumer (LocationService locationService) {
        this.locationService = locationService;
    }

    @KafkaListener(topics = "driver-locations", groupId = "location-service-group")
    public void consume(@Nullable DriverLocationUpdateEventDTO event) {
        if (event == null) {
            log.warn("Skipping malformed driver location message (deserialization failed)");
            return;
        }
        log.info("Receiving location: Driver {} @ [{} {}] @ {}", event.driverId(), event.lat(), event.lon(), event.timestamp());
        locationService.updateDriverLocation(event.driverId(), event.lat(), event.lon());
    }
}
