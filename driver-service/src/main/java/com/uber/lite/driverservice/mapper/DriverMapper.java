package com.uber.lite.driverservice.mapper;

import com.uber.lite.driverservice.dto.DriverRequestDTO;
import com.uber.lite.driverservice.dto.DriverResponseDTO;
import com.uber.lite.driverservice.model.Driver;
import com.uber.lite.driverservice.model.DriverStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DriverMapper {
    public Driver toModel(DriverRequestDTO driverRequestDTO) {
        if (driverRequestDTO == null) {
            return null;
        }

        return Driver.builder()
                .name(driverRequestDTO.name())
                .licensePlate(driverRequestDTO.licensePlate())
                .vehicleType(driverRequestDTO.vehicleType())
                .status(DriverStatus.OFFLINE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public DriverResponseDTO toResponseDTO(Driver driver) {
        if (driver == null) {
            return null;
        }

        return new DriverResponseDTO(
                driver.getId(),
                driver.getName(),
                driver.getLicensePlate(),
                driver.getVehicleType(),
                driver.getStatus().name(),
                driver.getCreatedAt()
        );
    }
}
