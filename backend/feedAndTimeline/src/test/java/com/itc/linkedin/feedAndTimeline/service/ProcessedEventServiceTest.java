package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.entity.ProcessedEvent;
import com.itc.linkedin.feedAndTimeline.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedEventServiceTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private ProcessedEventService processedEventService;

    @Captor
    private ArgumentCaptor<ProcessedEvent> eventCaptor;

    @Test
    void shouldReturnFalseForBlankEventId() {
        assertThat(processedEventService.isAlreadyProcessed("")).isFalse();
        verify(processedEventRepository, never()).existsByConsumerNameAndEventId("feed-timeline-consumer", "");
    }

    @Test
    void shouldSaveProcessedEventWhenNotAlreadyProcessed() {
        when(processedEventRepository.existsByConsumerNameAndEventId("feed-timeline-consumer", "evt-1")).thenReturn(false);

        processedEventService.markProcessed("post.created", "evt-1", 1);

        verify(processedEventRepository).save(eventCaptor.capture());
        ProcessedEvent event = eventCaptor.getValue();
        assertThat(event.getConsumerName()).isEqualTo("feed-timeline-consumer");
        assertThat(event.getTopic()).isEqualTo("post.created");
        assertThat(event.getEventId()).isEqualTo("evt-1");
        assertThat(event.getEventVersion()).isEqualTo(1);
        assertThat(event.getProcessedAt()).isNotNull();
    }

    @Test
    void shouldSkipSaveWhenAlreadyProcessed() {
        when(processedEventRepository.existsByConsumerNameAndEventId("feed-timeline-consumer", "evt-2")).thenReturn(true);

        processedEventService.markProcessed("post.created", "evt-2", 1);

        verify(processedEventRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldIgnoreDuplicateRaceOnSave() {
        when(processedEventRepository.existsByConsumerNameAndEventId("feed-timeline-consumer", "evt-3")).thenReturn(false);
        doThrow(new DataIntegrityViolationException("duplicate")).when(processedEventRepository).save(org.mockito.ArgumentMatchers.any());

        processedEventService.markProcessed("post.created", "evt-3", 1);

        verify(processedEventRepository).save(org.mockito.ArgumentMatchers.any());
    }
}
