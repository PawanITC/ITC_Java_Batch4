package com.itc.linkedin.searchAndDiscover.repository;

import com.itc.linkedin.searchAndDiscover.document.CompanyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CompanySearchRepository extends ElasticsearchRepository<CompanyDocument, String> {
    List<CompanyDocument> findByNameContainingOrIndustryContainingOrLocationContaining(
            String name, String industry, String location
    );
}