package com.uber.lite.riderservice.repository;

import com.uber.lite.riderservice.modal.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiderRepository extends JpaRepository <Rider, UUID> {
    Optional<Rider> findByEmail(String email);
}
