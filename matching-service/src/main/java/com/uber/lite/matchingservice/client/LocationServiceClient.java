package com.uber.lite.matchingservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocationServiceClient {
    private final WebClient webClient;

    @Value("${clients.location.base-url}")
    private String locationBaseUrl;

    public List<UUID> nearbyDrivers(double lat, double lon, double radiusKm) {
        String[] ids = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(locationBaseUrl.replace("http://", ""))
                        .path("/v1/locations/nearby")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("radiusKm", radiusKm)
                        .build())
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        if (ids == null) {
            return List.of();
        }

        return Arrays.stream(ids)
                .map(UUID::fromString)
                .toList();
    }
}
