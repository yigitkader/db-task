package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.CacheEntry
import com.recepyigitkader.deutchebankwork.dto.CacheStats
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentMap

@Service
class CacheService(
    private val cacheManager: CacheManager
) {

    fun getCacheStats(): Map<String, CacheStats> {
        val result = mutableMapOf<String, CacheStats>()

        cacheManager.cacheNames.forEach { cacheName ->
            val cache = cacheManager.getCache(cacheName)
            cache?.let {
                val nativeCache = it.nativeCache
                if (nativeCache is ConcurrentMap<*, *>) {
                    result[cacheName] = CacheStats(
                        cacheName = cacheName,
                        size = nativeCache.size,
                        entries = nativeCache.entries.map { entry ->
                            CacheEntry(
                                key = entry.key.toString(),
                                value = entry.value.toString()
                            )
                        }
                    )
                }
            }
        }
        return result
    }
}