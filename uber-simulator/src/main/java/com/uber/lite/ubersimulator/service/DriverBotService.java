package com.uber.lite.ubersimulator.service;

import com.uber.lite.common.event.DriverLocationUpdateEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverBotService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public void launchDriverBot(UUID driverId) {
        executor.submit(() -> {
            double lat = 40.7128;
            double lon = -74.0060;

            while (!Thread.currentThread().isInterrupted()) {
                lat += (Math.random() - 0.5) * 0.002;
                lon += (Math.random() - 0.5) * 0.002;

                DriverLocationUpdateEventDTO event = new DriverLocationUpdateEventDTO(
                        driverId, lat, lon, Instant.now()
                );

                kafkaTemplate.send("driver-locations", driverId.toString(), event)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send location update for driver {}", driverId, ex);
                            }
                            else {
                                log.info("Sent location update for driver {} to partition {} offset {}",
                                        driverId,
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());
                            }
                        });

                try {
                    Thread.sleep(3000 + (long)(Math.random() * 2000));
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
