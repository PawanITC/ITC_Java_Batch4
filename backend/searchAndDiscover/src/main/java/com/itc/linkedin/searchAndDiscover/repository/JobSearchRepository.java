package com.itc.linkedin.searchAndDiscover.repository;

import com.itc.linkedin.searchAndDiscover.document.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface JobSearchRepository extends ElasticsearchRepository<JobDocument, String> {
    List<JobDocument> findByTitleContainingOrCompanyNameContainingOrLocationContaining(
            String title, String companyName, String location
    );
}