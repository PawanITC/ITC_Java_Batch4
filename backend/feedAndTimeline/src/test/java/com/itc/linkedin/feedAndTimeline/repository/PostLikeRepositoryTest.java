package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.PostLike;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    void shouldCheckIfUserAlreadyLikedPost() {
        PostLike like = PostLike.builder()
                .postId(1L)
                .userId("user-1")
                .createdAt(LocalDateTime.now())
                .build();

        postLikeRepository.save(like);

        boolean exists = postLikeRepository.existsByPostIdAndUserId(1L, "user-1");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindLikeByPostIdAndUserId() {
        PostLike like = PostLike.builder()
                .postId(1L)
                .userId("user-1")
                .createdAt(LocalDateTime.now())
                .build();

        postLikeRepository.save(like);

        assertThat(postLikeRepository.findByPostIdAndUserId(1L, "user-1"))
                .isPresent();
    }

    @Test
    void shouldDeleteLikesByPostId() {
        postLikeRepository.save(PostLike.builder()
                .postId(1L)
                .userId("user-1")
                .createdAt(LocalDateTime.now())
                .build());

        postLikeRepository.deleteByPostId(1L);

        assertThat(postLikeRepository.existsByPostIdAndUserId(1L, "user-1"))
                .isFalse();
    }
}