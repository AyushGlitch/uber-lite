package com.uber.lite.ubersimulator.task;

import com.uber.lite.common.request.RideRequestDTO;
import com.uber.lite.ubersimulator.config.SimulationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemandGenerator {
    private final WebClient webClient;
    private final SimulationStore store;

    @Scheduled(fixedRate = 15000)
    public void createRandomRideRequest() {
        if (store.getDriverIds().isEmpty()) {
            log.info("No drivers available, skipping demand generation.");
            return;
        }
        if (store.getRiderIds().isEmpty()) {
            log.info("No riders available, skipping demand generation.");
            return;
        }

        UUID riderId = store.getRiderIds().get(
                new Random().nextInt(store.getRiderIds().size())
        );

        RideRequestDTO request = new RideRequestDTO(
                riderId,
                40.7128 + (Math.random() - 0.5) * 0.02,
                -74.0060 + (Math.random() - 0.5) * 0.02,
                40.7128 + (Math.random() - 0.5) * 0.02,
                -74.0060 + (Math.random() - 0.5) * 0.02
        );

        webClient.post()
                .uri("http://localhost:8081/v1/riders/rides")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        v -> log.info("Generated ride request for rider {}", riderId),
                        ex -> log.error("Failed to generate ride request for rider {}", riderId, ex)
                );
    }
}
