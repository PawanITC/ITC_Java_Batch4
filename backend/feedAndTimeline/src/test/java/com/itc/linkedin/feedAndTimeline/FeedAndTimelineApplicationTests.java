package com.itc.linkedin.feedAndTimeline;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
		properties = {
				"spring.datasource.url=jdbc:h2:mem:testdb",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=create-drop",

				"KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/test",
				"KEYCLOAK_JWK_SET_URI=http://localhost:8080/realms/test/protocol/openid-connect/certs"
		})
class FeedAndTimelineApplicationTests {

	@Test
	void contextLoads() {
	}
}