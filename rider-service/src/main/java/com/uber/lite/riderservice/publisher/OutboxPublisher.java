package com.uber.lite.riderservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.lite.common.event.RideRequestEventDTO;
import com.uber.lite.riderservice.model.OutboxEvent;
import com.uber.lite.riderservice.model.OutboxStatus;
import com.uber.lite.riderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:1000}")
    public void publishOutboxEvent() {
        List<OutboxEvent> pending = outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pending) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), RideRequestEventDTO.class);
                kafkaTemplate.send("ride-requests", event.getAggregateId().toString(), payload);
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(java.time.LocalDateTime.now());
            }
            catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                }
            }
            finally {
                outboxEventRepository.save(event);
            }
        }
    }
}
