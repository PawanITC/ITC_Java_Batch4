package com.itc.linkedin.searchAndDiscover.config;

import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DemoSearchSeedConfig {

    private final SearchService searchService;

    @Bean
    @ConditionalOnProperty(
            name = "app.search.seed-demo-data",
            havingValue = "true",
            matchIfMissing = false
    )
    CommandLineRunner demoSearchSeeder() {
        return args -> {
            if (searchService.hasSeedData()) {
                log.info("Skipping search demo data seed because indexes already contain documents");
                return;
            }

            searchService.seedAll();
            log.info("Seeded demo search data");
        };
    }
}
