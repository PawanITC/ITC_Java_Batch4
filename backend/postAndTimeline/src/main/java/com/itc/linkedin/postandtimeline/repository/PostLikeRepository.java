package com.itc.linkedin.postandtimeline.repository;

import com.itc.linkedin.postandtimeline.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, String userId);

    long countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, String userId);

    void deleteByPostId(Long postId);
}
