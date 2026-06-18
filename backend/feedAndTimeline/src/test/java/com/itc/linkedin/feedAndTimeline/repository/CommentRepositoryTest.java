package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldFindCommentsByPostIdOrderedByCreatedAtAsc() {
        Comment first = Comment.builder()
                .postId(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .content("First comment")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();

        Comment second = Comment.builder()
                .postId(1L)
                .authorId("user-2")
                .authorName("Another User")
                .content("Second comment")
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(first);
        commentRepository.save(second);

        List<Comment> comments =
                commentRepository.findByPostIdOrderByCreatedAtAsc(1L);

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("First comment");
        assertThat(comments.get(1).getContent()).isEqualTo("Second comment");
    }

    @Test
    void shouldDeleteCommentsByPostId() {
        commentRepository.save(Comment.builder()
                .postId(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .content("Comment")
                .createdAt(LocalDateTime.now())
                .build());

        commentRepository.deleteByPostId(1L);

        List<Comment> comments =
                commentRepository.findByPostIdOrderByCreatedAtAsc(1L);

        assertThat(comments).isEmpty();
    }
}