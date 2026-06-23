package com.itc.linkedin.feedAndTimeline.kafka.avro;

import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostCreatedAvroMapper {

    private static final String SCHEMA_JSON = """
            {
              "type": "record",
              "name": "PostCreatedEvent",
              "namespace": "com.itc.linkedin.events",
              "fields": [
                {"name": "eventId", "type": "string"},
                {"name": "eventType", "type": "string"},
                {"name": "eventVersion", "type": "int"},
                {"name": "occurredAt", "type": "string"},
                {"name": "postId", "type": "long"},
                {"name": "authorId", "type": "string"},
                {"name": "authorName", "type": "string"},
                {"name": "authorHeadline", "type": "string"},
                {"name": "content", "type": "string"},
                {"name": "createdAt", "type": "string"}
              ]
            }
            """;

    private final Schema schema = new Schema.Parser().parse(SCHEMA_JSON);

    public GenericRecord toGenericRecord(PostCreatedEvent event) {
        GenericRecord record = new GenericData.Record(schema);
        record.put("eventId", event.eventId());
        record.put("eventType", event.eventType());
        record.put("eventVersion", event.eventVersion());
        record.put("occurredAt", event.occurredAt().toString());
        record.put("postId", event.postId());
        record.put("authorId", event.authorId());
        record.put("authorName", event.authorName());
        record.put("authorHeadline", event.authorHeadline());
        record.put("content", event.content());
        record.put("createdAt", event.createdAt().toString());
        return record;
    }

    public PostCreatedEvent fromGenericRecord(GenericRecord record) {
        return new PostCreatedEvent(
                record.get("eventId").toString(),
                record.get("eventType").toString(),
                (Integer) record.get("eventVersion"),
                LocalDateTime.parse(record.get("occurredAt").toString()),
                (Long) record.get("postId"),
                record.get("authorId").toString(),
                record.get("authorName").toString(),
                record.get("authorHeadline").toString(),
                record.get("content").toString(),
                LocalDateTime.parse(record.get("createdAt").toString())
        );
    }

    public Schema schema() {
        return schema;
    }
}
