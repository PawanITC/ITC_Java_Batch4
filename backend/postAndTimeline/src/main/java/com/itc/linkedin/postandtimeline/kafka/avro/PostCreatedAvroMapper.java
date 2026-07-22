package com.itc.linkedin.postandtimeline.kafka.avro;

import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;

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
                {"name": "mediaObjectKey", "type": ["null", "string"], "default": null},
                {"name": "mediaUrl", "type": ["null", "string"], "default": null},
                {"name": "mediaType", "type": ["null", "string"], "default": null},
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
        record.put("mediaObjectKey", event.mediaObjectKey());
        record.put("mediaUrl", null);
        record.put("mediaType", event.mediaType());
        record.put("createdAt", event.createdAt().toString());
        return record;
    }

    public Schema schema() {
        return schema;
    }
}
