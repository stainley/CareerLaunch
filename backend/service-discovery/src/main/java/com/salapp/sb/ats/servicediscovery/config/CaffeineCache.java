package com.salapp.sb.ats.servicediscovery.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineCache {

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
