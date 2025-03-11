package com.salapp.sb.ats.servicediscovery.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link CaffeineCache} configuration class.
 * Verifies that the CacheManager bean is correctly instantiated and configured.
 *
 * @author Stainley Lebron
 * @since 1.0.0
 */
class CaffeineCacheTest {

    @Test
    void testCacheManagerBeanCreation() {
        // Arrange
        CaffeineCache caffeineCacheConfig = new CaffeineCache();

        // Act
        CacheManager cacheManager = caffeineCacheConfig.cacheManager();

        // Assert
        assertNotNull(cacheManager, "CacheManager should not be null");
        assertTrue(cacheManager instanceof CaffeineCacheManager,
                "CacheManager should be an instance of CaffeineCacheManager");
    }
}
