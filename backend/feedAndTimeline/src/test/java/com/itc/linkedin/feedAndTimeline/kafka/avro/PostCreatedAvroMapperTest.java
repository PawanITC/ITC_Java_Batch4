package com.itc.linkedin.feedAndTimeline.kafka.avro;

import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostCreatedAvroMapperTest {

    private final PostCreatedAvroMapper mapper = new PostCreatedAvroMapper();

    @Test
    void shouldMapGenericRecordToEvent() {
        GenericRecord record = new GenericData.Record(mapper.schema());
        record.put("eventId", "evt-20");
        record.put("eventType", "post.created");
        record.put("eventVersion", 1);
        record.put("occurredAt", "2026-06-23T18:00:00");
        record.put("postId", 88L);
        record.put("authorId", "user.demo");
        record.put("authorName", "user.demo");
        record.put("authorHeadline", "LinkedIn Member");
        record.put("content", "Avro in feed");
        record.put("createdAt", "2026-06-23T18:00:00");

        PostCreatedEvent event = mapper.fromGenericRecord(record);

        assertThat(event.eventId()).isEqualTo("evt-20");
        assertThat(event.eventType()).isEqualTo("post.created");
        assertThat(event.eventVersion()).isEqualTo(1);
        assertThat(event.postId()).isEqualTo(88L);
        assertThat(event.occurredAt()).isEqualTo(LocalDateTime.of(2026, 6, 23, 18, 0));
    }
}
