package com.uber.lite.riderservice.service;

import com.uber.lite.riderservice.dto.RiderRequestDTO;
import com.uber.lite.riderservice.dto.RiderResponseDTO;
import com.uber.lite.riderservice.mapper.RiderMapper;
import com.uber.lite.riderservice.model.Rider;
import com.uber.lite.riderservice.repository.RiderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RiderService {
    private final RiderRepository riderRepository;
    private final RiderMapper riderMapper;

    public RiderService (RiderRepository riderRepository, RiderMapper riderMapper) {
        this.riderRepository = riderRepository;
        this.riderMapper = riderMapper;
    }

    public RiderResponseDTO registerRider(RiderRequestDTO riderRequestDTO) {
        riderRepository.findByEmail(riderRequestDTO.email()).ifPresent(
                rider -> {
                    throw new RuntimeException("Rider with email " + riderRequestDTO.email() + " already exists");
                }
        );

        Rider rider = riderMapper.toModal(riderRequestDTO);
        Rider savedRider = riderRepository.save(rider);

        return riderMapper.toDto(savedRider);
    }

    public RiderResponseDTO getRiderById(UUID id) {
        Rider rider = riderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rider with id " + id + " not found")
        );

        return riderMapper.toDto(rider);
    }
}
