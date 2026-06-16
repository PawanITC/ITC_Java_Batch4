package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();
}