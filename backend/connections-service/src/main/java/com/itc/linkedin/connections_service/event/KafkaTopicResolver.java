package com.itc.linkedin.connections_service.event;
import com.itc.linkedin.connections_service.config.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTopicResolver {

    private final KafkaTopicProperties topicProperties;

    public String resolveTopic(String eventType) {
        ConnectionEventType type = ConnectionEventType.valueOf(eventType);

        return switch (type) {
            case CONNECTION_REQUEST_SENT -> topicProperties.getConnectionRequestSent();
            case CONNECTION_REQUEST_ACCEPTED -> topicProperties.getConnectionRequestAccepted();
            case CONNECTION_REQUEST_REJECTED -> topicProperties.getConnectionRequestRejected();
            case CONNECTION_REQUEST_CANCELLED -> topicProperties.getConnectionRequestCancelled();
            case USER_BLOCKED -> topicProperties.getUserBlocked();
            case USER_UNBLOCKED -> topicProperties.getUserUnblocked();
        };
    }
}