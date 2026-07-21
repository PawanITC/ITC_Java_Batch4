package com.itclinkedin.userprofile.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic followEventsTopic() {
        return TopicBuilder.name("social-follow-events")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic profileCreatedTopic() {
        return TopicBuilder.name("profile.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic profileUpdatedTopic() {
        return TopicBuilder.name("profile.updated")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic profileDeletedTopic() {
        return TopicBuilder.name("profile.deleted")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
