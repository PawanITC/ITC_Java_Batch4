package com.itc.linkedin.searchAndDiscover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SearchAndDiscoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchAndDiscoverApplication.class, args);
	}

}
