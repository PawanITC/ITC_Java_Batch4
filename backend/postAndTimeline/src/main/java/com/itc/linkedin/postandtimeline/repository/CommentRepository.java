package com.itc.linkedin.postandtimeline.repository;

import com.itc.linkedin.postandtimeline.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
