package com.recepyigitkader.deutchebankwork.config

import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

@Configuration
@EnableCaching
class CacheConfig {

    private val logger = LoggerFactory.getLogger(CacheConfig::class.java)

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = ConcurrentMapCacheManager()
        cacheManager.setCacheNames(
            listOf(
                "facts",
                "facts_all",
                "analytics"
            )
        )
        return cacheManager
    }

    @Scheduled(fixedRate = ONE_DAY)
    fun evictAllCaches() {
        cacheManager().cacheNames.forEach {
            cacheManager().getCache(it)?.clear()
        }
        logger.info("Cached Evicted ${LocalDateTime.now()}")
    }


    companion object {
        const val ONE_DAY = 3600000 * 24L
        const val ONE_MINUTE = 60000L
    }

}