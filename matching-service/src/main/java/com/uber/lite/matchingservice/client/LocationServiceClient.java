package com.uber.lite.matchingservice.client;

import com.uber.lite.common.response.NearbyDriverResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationServiceClient {
    private final WebClient webClient;

    @Value("${clients.location.base-url}")
    private String locationBaseUrl;

    public List<NearbyDriverResponseDTO> nearbyDrivers(double lat, double lon, double radiusKm) {
        NearbyDriverResponseDTO[] drivers = webClient.get()
                .uri(locationBaseUrl + "/v1/locations/nearby?lat={lat}&lon={lon}&radiusKm={radiusKm}",
                        lat, lon, radiusKm)
                .retrieve()
                .bodyToMono(NearbyDriverResponseDTO[].class)
                .block();

        if (drivers == null) {
            return List.of();
        }

        return Arrays.stream(drivers).toList();
    }
}
