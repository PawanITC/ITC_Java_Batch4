package com.itc.linkedin.connections_service.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connection-service.kafka.topics")
public class KafkaTopicProperties {

    private String connectionRequestSent;
    private String connectionRequestAccepted;
    private String connectionRequestRejected;
    private String connectionRequestCancelled;
    private String userBlocked;
    private String userUnblocked;
}