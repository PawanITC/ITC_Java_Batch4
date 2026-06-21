package com.itclinkedin.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaConfig {

    @Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<Object,Object>kafkaTemplate){
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate);
    ExponentialBackOff backOff = new ExponentialBackOff(1000L,2.0);
    backOff.setMaxAttempts(3);

    return new DefaultErrorHandler(recoverer,backOff);
}
}
