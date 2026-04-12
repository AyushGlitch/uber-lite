package com.uber.lite.locationservice.service;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    void updateDriverLocation(UUID driverId, double lat, double lon);
    List<String> findNearbyDrivers(double lat, double lon, double radiusKm);
}
