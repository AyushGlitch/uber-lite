package com.uber.lite.locationservice.controller;

import com.uber.lite.locationservice.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/nearby")
    public List<String> getNearbyDrivers(@RequestParam double lat,
                                         @RequestParam double lon,
                                         @RequestParam(defaultValue = "5") double radiusKm) {
        return locationService.findNearbyDrivers(lat, lon, radiusKm);
    }
}
