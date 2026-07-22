package com.itc.linkedin.postandtimeline.repository;

import com.itc.linkedin.postandtimeline.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByIdAndAuthorId(Long id, String authorId);
}
