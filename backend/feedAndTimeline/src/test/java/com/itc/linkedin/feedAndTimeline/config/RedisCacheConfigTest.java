package com.itc.linkedin.feedAndTimeline.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RedisCacheConfigTest {

    private final JacksonConfig jacksonConfig = new JacksonConfig();

    @Test
    void shouldSerializeTimelineResponsesWithJavaTimeFields() {
        ObjectMapper redisObjectMapper = jacksonConfig.objectMapper().copy();
        redisObjectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.lang")
                        .allowIfSubType("java.util")
                        .allowIfSubType("java.time")
                        .allowIfSubType("com.itc.linkedin.feedAndTimeline")
                        .build(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        List<TimelinePostResponse> timeline = List.of(
                TimelinePostResponse.builder()
                        .postId(10L)
                        .authorId("user.demo")
                        .authorName("Demo User")
                        .authorHeadline("Engineer")
                        .content("hello")
                        .likesCount(3)
                        .commentsCount(1)
                        .createdAt(LocalDateTime.of(2026, 6, 23, 21, 15, 0))
                        .build()
        );

        byte[] serialized = serializer.serialize(timeline);
        Object deserialized = serializer.deserialize(serialized);

        assertNotNull(serialized);
        assertInstanceOf(List.class, deserialized);
        TimelinePostResponse cachedPost = (TimelinePostResponse) ((List<?>) deserialized).getFirst();
        assertEquals(10L, cachedPost.postId());
        assertEquals("user.demo", cachedPost.authorId());
        assertEquals(LocalDateTime.of(2026, 6, 23, 21, 15, 0), cachedPost.createdAt());
    }
}
