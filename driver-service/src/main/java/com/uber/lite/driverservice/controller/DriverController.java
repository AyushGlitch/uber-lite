package com.uber.lite.driverservice.controller;

import com.uber.lite.driverservice.dto.DriverRequestDTO;
import com.uber.lite.driverservice.dto.DriverResponseDTO;
import com.uber.lite.driverservice.model.DriverStatus;
import com.uber.lite.driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController (DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> registerDriver(@Valid @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO registeredDriver = driverService.registerDriver(driverRequestDTO);
        return ResponseEntity.ok().body(registeredDriver);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriver(@PathVariable UUID id) {
        DriverResponseDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok().body(driver);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateDriverStatus(@PathVariable UUID id, @RequestParam DriverStatus status) {
        driverService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
