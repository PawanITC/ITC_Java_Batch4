package com.itc.linkedin.feedAndTimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processed_event_consumer_event",
                        columnNames = {"consumerName", "eventId"}
                )
        },
        indexes = {
                @Index(name = "idx_processed_event_topic", columnList = "topic")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String consumerName;

    private String topic;

    private String eventId;

    private Integer eventVersion;

    private LocalDateTime processedAt;
}
