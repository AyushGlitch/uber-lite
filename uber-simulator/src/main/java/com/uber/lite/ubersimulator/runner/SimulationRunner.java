package com.uber.lite.ubersimulator.runner;

import com.uber.lite.common.request.DriverRequestDTO;
import com.uber.lite.common.request.RiderRequestDTO;
import com.uber.lite.common.response.DriverResponseDTO;
import com.uber.lite.common.response.RiderResponseDTO;
import com.uber.lite.ubersimulator.config.SimulationStore;
import com.uber.lite.ubersimulator.service.DriverBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulationRunner implements CommandLineRunner {
    private final WebClient webClient;
    private final SimulationStore store;
    private final DriverBotService driverBotService;

    @Override
    public void run(String ...args) {
        log.info("Starting Uber Simulator...");

        for (int i= 0; i< 10; i++) {
            RiderRequestDTO dto = new RiderRequestDTO("Rider_" + i, "rider" + i + "@test.com");
            RiderResponseDTO responseDTO = webClient.post()
                    .uri("http://localhost:8081/v1/riders")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(RiderResponseDTO.class)
                    .block();
        }

        for (int i= 0; i< 50; i++) {
            DriverRequestDTO dto = new DriverRequestDTO("Driver_" + i, "ABC123" + i, "SEDAN");
            DriverResponseDTO responseDTO = webClient.post()
                    .uri("http://localhost:8082/v1/drivers")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(DriverResponseDTO.class)
                    .block();

            if (responseDTO != null) {
                store.addDriver(responseDTO.id());
                webClient.patch()
                        .uri("http://localhost:8082/v1/drivers/" + responseDTO.id() + "/status?status=ONLINE")
                        .retrieve()
                        .toBodilessEntity()
                        .block();

                driverBotService.launchDriverBot(responseDTO.id());
            }
        }
        log.info("Simulation setup complete. 10 riders and 50 drivers created, driver bots launched.");
    }
}
