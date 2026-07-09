package com.itc.linkedin.searchAndDiscover.kafka.avro;

import com.itc.linkedin.searchAndDiscover.kafka.event.PostCreatedEvent;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostCreatedAvroMapper {

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
                nullableString(fieldValue(record, "mediaObjectKey")),
                nullableString(fieldValue(record, "mediaType")),
                LocalDateTime.parse(record.get("createdAt").toString())
        );
    }

    private String nullableString(Object value) {
        return value == null ? null : value.toString();
    }

    private Object fieldValue(GenericRecord record, String fieldName) {
        return record.getSchema().getField(fieldName) == null ? null : record.get(fieldName);
    }
}
