package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class UrlShortenerServiceTest {

    @Mock
    private var factRepository: FactRepository = mock(FactRepository::class.java)

    @Autowired
    private lateinit var urlShortenerService: UrlShortenerService

    @Test
    fun test(){

    }

    @Test
    fun generateShortUrlTest() {
        `when`(factRepository.existsFactByShortenedUrl(anyString()))
            .thenReturn(false)

        val shortUrl = urlShortenerService.generateShortUrl()

        assertEquals(7, shortUrl.length)
        assertTrue(urlShortenerService.isValidShortUrl(shortUrl))
        verify(factRepository).existsFactByShortenedUrl(shortUrl)
    }

    @Test
    fun validateShortUrlTest() {
        assertTrue(urlShortenerService.isValidShortUrl("Ab2Xy9z"))

        assertFalse(urlShortenerService.isValidShortUrl("abc123"))

        assertFalse(urlShortenerService.isValidShortUrl("ab#1234"))
    }

    @Test
    fun generateUniqueUrlTest() {
        Mockito.`when`(factRepository.existsFactByShortenedUrl(anyString()))
            .thenReturn(true)
            .thenReturn(false)

        // When
        val shortUrl = urlShortenerService.generateShortUrl()

        // Then
        assertEquals(7, shortUrl.length)
        verify(factRepository, times(2)).existsFactByShortenedUrl(anyString())
    }

}