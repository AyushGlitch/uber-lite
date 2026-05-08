package com.uber.lite.locationservice.service;

import com.uber.lite.common.response.NearbyDriverResponseDTO;
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
    public List<NearbyDriverResponseDTO> findNearbyDrivers(double lat, double lon, double radiusKm) {
        Circle circle = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .includeCoordinates();

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate
                .opsForGeo().search(DRIVER_GEO_KEY, circle, args);

        return results.getContent().stream()
                .filter(res -> res.getContent() != null && res.getContent().getPoint() != null)
                .map(res -> new NearbyDriverResponseDTO(
                        UUID.fromString(res.getContent().getName()),
                        res.getContent().getPoint().getY(),
                        res.getContent().getPoint().getX()
                ))
                .toList();
    }
}
