package com.uber.lite.riderservice.repository;

import com.uber.lite.riderservice.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {

}
