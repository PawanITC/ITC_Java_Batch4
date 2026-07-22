package com.itc.linkedin.feedAndTimeline.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic postCreatedTopic() {
        return TopicBuilder.name("post.created").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic postDeletedTopic() {
        return TopicBuilder.name("post.deleted").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic postLikedTopic() {
        return TopicBuilder.name("post.liked").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic commentCreatedTopic() {
        return TopicBuilder.name("comment.created").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notifications").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic socialUnfollowEventsTopic() {
        return TopicBuilder.name("social-unfollow-events").partitions(4).replicas(1).build();
    }

    @Bean
    public NewTopic postCreatedDltTopic() {
        return TopicBuilder.name("post.created.dlt").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic postDeletedDltTopic() {
        return TopicBuilder.name("post.deleted.dlt").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic postLikedDltTopic() {
        return TopicBuilder.name("post.liked.dlt").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic commentCreatedDltTopic() {
        return TopicBuilder.name("comment.created.dlt").partitions(3).replicas(1).build();
    }
}
