package com.salapp.sb.ats.servicediscovery.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for establishing the Caffeine caching infrastructure
 * within the Spring Boot application. This class defines the necessary bean definitions
 * to enable caching functionality using the Caffeine cache implementation.
 *
 * @author Stainley Lebron
 * @since 1.0.0
 */
@Configuration
public class CaffeineCache {

    /**
     * Constructs and initializes a CacheManager instance utilizing the Caffeine caching
     * framework. This method creates a bean that manages cache operations throughout
     * the application, providing an efficient in-memory caching solution.
     *
     * @return CacheManager instance configured with Caffeine implementation
     */
    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
