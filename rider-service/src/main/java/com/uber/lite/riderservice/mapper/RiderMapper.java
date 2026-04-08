package com.uber.lite.riderservice.mapper;

import com.uber.lite.riderservice.dto.RiderRequestDTO;
import com.uber.lite.riderservice.dto.RiderResponseDTO;
import com.uber.lite.riderservice.model.Rider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RiderMapper {
    public Rider toModal(RiderRequestDTO riderRequestDTO) {
        if (riderRequestDTO == null) {
            return null;
        }

        return Rider.builder()
                .name(riderRequestDTO.name())
                .email(riderRequestDTO.email())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public RiderResponseDTO toDto(Rider rider) {
        if (rider == null) {
            return null;
        }

        return new RiderResponseDTO(
                rider.getId(),
                rider.getName(),
                rider.getEmail(),
                rider.getCreatedAt()
        );
    }
}
