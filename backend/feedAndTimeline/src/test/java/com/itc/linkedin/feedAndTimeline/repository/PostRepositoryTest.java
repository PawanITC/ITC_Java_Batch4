package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindAllPostsOrderedByCreatedAtDesc() {
        Post olderPost = Post.builder()
                .authorId("user-1")
                .authorName("Demo User")
                .authorHeadline("Developer")
                .content("Older post")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Post newerPost = Post.builder()
                .authorId("user-2")
                .authorName("Another User")
                .authorHeadline("Engineer")
                .content("Newer post")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(olderPost);
        postRepository.save(newerPost);

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        assertThat(posts).hasSize(2);
        assertThat(posts.get(0).getContent()).isEqualTo("Newer post");
        assertThat(posts.get(1).getContent()).isEqualTo("Older post");
    }
}