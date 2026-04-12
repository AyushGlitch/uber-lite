package com.uber.lite.locationservice.service;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LocationServiceImpl implements LocationService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String DRIVER_GEO_KEY = "active_drivers";

    public LocationServiceImpl (RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void updateDriverLocation(UUID driverId, double lat, double lon) {
        Point point = new Point(lon, lat); // Redis Geo uses (lon, lat) order
        redisTemplate.opsForGeo().add(DRIVER_GEO_KEY, point, driverId.toString());
    }

    @Override
    public List<String> findNearbyDrivers(double lat, double lon, double radiusKm) {
        Circle circle = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate
                .opsForGeo().search(DRIVER_GEO_KEY, circle);

        return results.getContent().stream()
                .map(res -> res.getContent().getName())
                .toList();
    }
}
