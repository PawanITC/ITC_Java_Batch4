package com.itc.linkedin.searchAndDiscover.repository;

import com.itc.linkedin.searchAndDiscover.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, String> {
    List<PostDocument> findByAuthorNameContainingOrContentContaining(
            String authorName, String content
    );
}