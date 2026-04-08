package com.uber.lite.driverservice.repository;

import com.uber.lite.driverservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByLicensePlate(String licensePlate);
}
