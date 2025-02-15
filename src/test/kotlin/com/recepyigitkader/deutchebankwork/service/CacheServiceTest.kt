package com.recepyigitkader.deutchebankwork.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CacheServiceTest {

    @Mock
    private lateinit var cacheManager: CacheManager

    @Mock
    private lateinit var cache: Cache

    @InjectMocks
    private lateinit var cacheService: CacheService

    @Test
    fun `should return cache statistics`() {
        // given
        val cacheName = "testCache"
        val cacheNames = setOf(cacheName)
        val mockNativeCache = ConcurrentHashMap<String, String>()
        mockNativeCache["key1"] = "value1"
        mockNativeCache["key2"] = "value2"

        `when`(cacheManager.cacheNames).thenReturn(cacheNames)
        `when`(cacheManager.getCache(cacheName)).thenReturn(cache)
        `when`(cache.nativeCache).thenReturn(mockNativeCache)

        // when
        val result = cacheService.getCacheStats()

        // then
        assertNotNull(result[cacheName])
        assertEquals(cacheName, result[cacheName]?.cacheName)
        assertEquals(2, result[cacheName]?.size)
        assertEquals(2, result[cacheName]?.entries?.size)

        val entries = result[cacheName]?.entries
        assertTrue(entries?.any { it.key == "key1" && it.value == "value1" } == true)
        assertTrue(entries?.any { it.key == "key2" && it.value == "value2" } == true)

        verify(cacheManager).cacheNames
        verify(cacheManager).getCache(cacheName)
        verify(cache).nativeCache
    }

    @Test
    fun `should return empty map when no caches exist`() {
        // given
        `when`(cacheManager.cacheNames).thenReturn(emptySet())

        // when
        val result = cacheService.getCacheStats()

        // then
        assertTrue(result.isEmpty())
        verify(cacheManager).cacheNames
        verify(cacheManager, never()).getCache(any())
    }

    @Test
    fun `should handle null cache gracefully`() {
        // given
        val cacheName = "testCache"
        `when`(cacheManager.cacheNames).thenReturn(setOf(cacheName))
        `when`(cacheManager.getCache(cacheName)).thenReturn(null)

        // when
        val result = cacheService.getCacheStats()

        // then
        assertTrue(result.isEmpty())
        verify(cacheManager).cacheNames
        verify(cacheManager).getCache(cacheName)
    }

    @Test
    fun `should skip non-ConcurrentMap caches`() {
        // given
        val cacheName = "testCache"
        `when`(cacheManager.cacheNames).thenReturn(setOf(cacheName))
        `when`(cacheManager.getCache(cacheName)).thenReturn(cache)
        `when`(cache.nativeCache).thenReturn("not a concurrent map")

        // when
        val result = cacheService.getCacheStats()

        // then
        assertTrue(result.isEmpty())
        verify(cacheManager).cacheNames
        verify(cacheManager).getCache(cacheName)
        verify(cache).nativeCache
    }
}