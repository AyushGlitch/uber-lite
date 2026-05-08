package com.uber.lite.locationservice.service;

import com.uber.lite.common.response.NearbyDriverResponseDTO;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    void updateDriverLocation(UUID driverId, double lat, double lon);
    List<NearbyDriverResponseDTO> findNearbyDrivers(double lat, double lon, double radiusKm);
}
