package com.uber.lite.tripservice.service;

import com.uber.lite.tripservice.model.TripStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class TripStateMachineService {
    private static final Map<TripStatus, Set<TripStatus>> ALLOWED_TRANSITIONS = Map.of(
            TripStatus.PENDING, Set.of(TripStatus.MATCHED, TripStatus.CANCELLED),
            TripStatus.MATCHED, Set.of(TripStatus.EN_ROUTE, TripStatus.CANCELLED),
            TripStatus.EN_ROUTE, Set.of(TripStatus.COMPLETED, TripStatus.CANCELLED),
            TripStatus.COMPLETED, Set.of(),
            TripStatus.CANCELLED, Set.of()
    );

    public void assertTransition(TripStatus from, TripStatus to) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
            throw new IllegalStateException("Invalid trip transition: " + from + " -> " + to);
        }
    }
}
