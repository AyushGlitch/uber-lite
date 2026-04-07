package com.uber.lite.riderservice.controller;

import com.uber.lite.riderservice.dto.RiderRequestDTO;
import com.uber.lite.riderservice.dto.RiderResponseDTO;
import com.uber.lite.riderservice.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/riders")
@RequiredArgsConstructor
public class RiderController {
    private final RiderService riderService;

    @PostMapping
    public ResponseEntity<RiderResponseDTO> registerRider(@Valid @RequestBody RiderRequestDTO riderRequestDTO) {
        RiderResponseDTO registeredRider = riderService.registerRider(riderRequestDTO);
        return ResponseEntity.ok().body(registeredRider);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderResponseDTO> getRider(@PathVariable UUID id) {
        RiderResponseDTO rider = riderService.getRiderById(id);
        return ResponseEntity.ok().body(rider);
    }
}
