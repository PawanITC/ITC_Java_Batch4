package com.itclinkedin.jobPosting.scheduler;

import com.itclinkedin.jobPosting.entity.OutboxEvent;
import com.itclinkedin.jobPosting.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock private OutboxRepository outboxRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks private OutboxScheduler outboxScheduler;

    @Test
    void processOutboxEvents_Success() throws Exception {
        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId("id-123")
                .payload("{}")
                .processed(false)
                .build();

        when(outboxRepository.findByProcessedFalseOrderByCreatedAtAsc()).thenReturn(Collections.singletonList(event));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        outboxScheduler.processOutboxEvents();

        assertTrue(event.isProcessed());
        verify(outboxRepository, times(1)).save(event);
    }

    @Test
    void processOutboxEvents_Failure_KafkaConnectionTimeout() throws Exception {
        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId("id-123")
                .payload("{}")
                .processed(false)
                .build();

        when(outboxRepository.findByProcessedFalseOrderByCreatedAtAsc()).thenReturn(Collections.singletonList(event));

        // Emulate an internal infrastructure exception when evaluating the Future response (.get())
        CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka Broker Partition Unreachable"));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(failedFuture);

        outboxScheduler.processOutboxEvents();

        // The state remains unchanged across failure windows to enable retries
        assertFalse(event.isProcessed());
        verify(outboxRepository, never()).save(event);
    }
}