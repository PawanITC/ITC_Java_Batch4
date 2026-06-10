package com.itc.linkedin.searchAndDiscover.repository;

import com.itc.linkedin.searchAndDiscover.document.PeopleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PeopleSearchRepository extends ElasticsearchRepository<PeopleDocument, String> {

    List<PeopleDocument> findByFullNameContainingOrHeadlineContainingOrSkillsContaining(
            String fullName,
            String headline,
            String skills
    );
}
