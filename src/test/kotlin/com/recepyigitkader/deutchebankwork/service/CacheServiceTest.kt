package com.recepyigitkader.deutchebankwork.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class CacheServiceTest {

    private var cacheManager: CacheManager = mock(CacheManager::class.java)

    @Autowired
    private lateinit var cacheService: CacheService

    @Test
    fun `getCacheStats should return empty map when no caches exist`() {
        // Given
        `when`(cacheManager.cacheNames).thenReturn(emptySet())

        // When
        val result = cacheService.getCacheStats()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCacheStats should return correct stats for existing caches`() {
        // Given
        val cacheName = "testCache"
        val mockCache = mock<Cache>()
        val mockNativeCache = ConcurrentHashMap<String, String>()
        mockNativeCache["key1"] = "value1"
        mockNativeCache["key2"] = "value2"

        `when`(cacheManager.cacheNames).thenReturn(setOf(cacheName))
        `when`(cacheManager.getCache(cacheName)).thenReturn(mockCache)
        `when`(mockCache.nativeCache).thenReturn(mockNativeCache)

        // When
        val result = cacheService.getCacheStats()

        // Then
        assertNotNull(result[cacheName])
        assertEquals(cacheName, result[cacheName]?.cacheName)
        assertEquals(2, result[cacheName]?.size)
        assertEquals(2, result[cacheName]?.entries?.size)

        val entries = result[cacheName]?.entries
        assertTrue(entries?.any { it.key == "key1" && it.value == "value1" } == true)
        assertTrue(entries?.any { it.key == "key2" && it.value == "value2" } == true)
    }

    @Test
    fun `getCacheStats should handle null cache gracefully`() {
        // Given
        val cacheName = "testCache"
        `when`(cacheManager.cacheNames).thenReturn(setOf(cacheName))
        `when`(cacheManager.getCache(cacheName)).thenReturn(null)

        // When
        val result = cacheService.getCacheStats()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCacheStats should handle non-ConcurrentMap native cache`() {
        // Given
        val cacheName = "testCache"
        val mockCache = mock<Cache>()

        `when`(cacheManager.cacheNames).thenReturn(setOf(cacheName))
        `when`(cacheManager.getCache(cacheName)).thenReturn(mockCache)
        `when`(mockCache.nativeCache).thenReturn("not a concurrent map")

        // When
        val result = cacheService.getCacheStats()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCacheStats should handle multiple caches`() {
        // Given
        val cache1Name = "cache1"
        val cache2Name = "cache2"
        val mockCache1 = mock<Cache>()
        val mockCache2 = mock<Cache>()

        val nativeCache1 = ConcurrentHashMap<String, String>()
        nativeCache1["key1"] = "value1"

        val nativeCache2 = ConcurrentHashMap<String, String>()
        nativeCache2["key2"] = "value2"

        `when`(cacheManager.cacheNames).thenReturn(setOf(cache1Name, cache2Name))
        `when`(cacheManager.getCache(cache1Name)).thenReturn(mockCache1)
        `when`(cacheManager.getCache(cache2Name)).thenReturn(mockCache2)
        `when`(mockCache1.nativeCache).thenReturn(nativeCache1)
        `when`(mockCache2.nativeCache).thenReturn(nativeCache2)

        // When
        val result = cacheService.getCacheStats()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.containsKey(cache1Name))
        assertTrue(result.containsKey(cache2Name))
        assertEquals(1, result[cache1Name]?.size)
        assertEquals(1, result[cache2Name]?.size)
    }
}