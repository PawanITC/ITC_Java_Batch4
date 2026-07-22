package com.itc.linkedin.feedAndTimeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class FeedAndTimelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedAndTimelineApplication.class, args);
	}

}
