package com.itc.linkedin.connections_service;

import org.springframework.boot.SpringApplication;

public class TestConnectionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConnectionsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
