package com.itc.linkedin.postandtimeline.kafka.avro;

import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostCreatedAvroMapperTest {

    private final PostCreatedAvroMapper mapper = new PostCreatedAvroMapper();

    @Test
    void shouldMapEventToGenericRecord() {
        PostCreatedEvent event = new PostCreatedEvent(
                "evt-10",
                "post.created",
                1,
                LocalDateTime.of(2026, 6, 23, 18, 0),
                55L,
                "user.demo",
                "user.demo",
                "LinkedIn Member",
                "Avro payload",
                "posts/user.demo/photo.jpg",
                "IMAGE",
                LocalDateTime.of(2026, 6, 23, 18, 0)
        );

        GenericRecord record = mapper.toGenericRecord(event);

        assertThat(record.get("eventId").toString()).isEqualTo("evt-10");
        assertThat(record.get("eventType").toString()).isEqualTo("post.created");
        assertThat(record.get("eventVersion")).isEqualTo(1);
        assertThat(record.get("postId")).isEqualTo(55L);
        assertThat(record.get("authorId").toString()).isEqualTo("user.demo");
        assertThat(record.get("mediaObjectKey").toString()).isEqualTo("posts/user.demo/photo.jpg");
        assertThat(record.get("mediaType").toString()).isEqualTo("IMAGE");
    }
}
