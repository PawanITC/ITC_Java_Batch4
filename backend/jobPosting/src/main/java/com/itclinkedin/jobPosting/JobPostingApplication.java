package com.itclinkedin.jobPosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobPostingApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPostingApplication.class, args);
	}

}
