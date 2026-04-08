package com.uber.lite.driverservice.service;

import com.uber.lite.driverservice.dto.DriverRequestDTO;
import com.uber.lite.driverservice.dto.DriverResponseDTO;
import com.uber.lite.driverservice.mapper.DriverMapper;
import com.uber.lite.driverservice.model.Driver;
import com.uber.lite.driverservice.model.DriverStatus;
import com.uber.lite.driverservice.repository.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public DriverService(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    public DriverResponseDTO registerDriver(DriverRequestDTO driverRequestDTO) {
        driverRepository.findByLicensePlate(driverRequestDTO.licensePlate()).ifPresent(
                driver -> {
                    throw new RuntimeException("Driver with license plate " + driverRequestDTO.licensePlate() + " already exists");
                }
        );

        Driver driver = driverMapper.toModel(driverRequestDTO);
        Driver savedDriver = driverRepository.save(driver);

        return driverMapper.toResponseDTO(savedDriver);
    }

    public DriverResponseDTO getDriverById(UUID id) {
        Driver driver = driverRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Driver with id " + id + " not found")
        );

        return driverMapper.toResponseDTO(driver);
    }

    @Transactional
    public void updateStatus(UUID id, DriverStatus status) {
        Driver driver = driverRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Driver with id " + id + " not found")
        );

        driver.setStatus(status);
        driverRepository.save(driver);
    }
}
