package com.itc.linkedin.postandtimeline;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:postdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.schema-registry-url=mock://post-service-test",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/does-not-matter"
})
class PostAndTimelineApplicationTests {

    @Test
    void contextLoads() {
    }

}
