package com.itc.linkedin.postandtimeline.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    private String topic;

    @Column(length = 10000)
    private String payload;

    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}