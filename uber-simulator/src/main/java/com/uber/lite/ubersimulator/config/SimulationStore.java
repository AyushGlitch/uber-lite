package com.uber.lite.ubersimulator.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
public class SimulationStore {
    private final List<UUID> riderIds = new CopyOnWriteArrayList<>();
    private final List<UUID> driverIds = new CopyOnWriteArrayList<>();

    public void addRider(UUID id) {
        riderIds.add(id);
    }
    public void addDriver(UUID id) {
        driverIds.add(id);
    }
}
