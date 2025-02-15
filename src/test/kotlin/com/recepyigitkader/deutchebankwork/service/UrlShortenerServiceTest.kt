package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class UrlShortenerServiceTest {

    @Mock
    private lateinit var factRepository: FactRepository

    @InjectMocks
    private lateinit var urlShortenerService: UrlShortenerService

    @Test
    fun `should generate valid short url`() {
        // given
        `when`(factRepository.existsFactByShortenedUrl(anyString())).thenReturn(false)

        // when
        val shortUrl = urlShortenerService.generateShortUrl()

        // then
        assertEquals(7, shortUrl.length)
        assertTrue(urlShortenerService.isValidShortUrl(shortUrl))
        verify(factRepository).existsFactByShortenedUrl(shortUrl)
    }

    @Test
    fun `should regenerate url if exists`() {
        // given
        `when`(factRepository.existsFactByShortenedUrl(anyString()))
            .thenReturn(true)
            .thenReturn(false)

        // when
        val shortUrl = urlShortenerService.generateShortUrl()

        // then
        assertEquals(7, shortUrl.length)
        assertTrue(urlShortenerService.isValidShortUrl(shortUrl))
        verify(factRepository, times(2)).existsFactByShortenedUrl(anyString())
    }

    @Test
    fun `should validate short url length`() {
        // given & when & then
        assertFalse(urlShortenerService.isValidShortUrl("abc"))
        assertFalse(urlShortenerService.isValidShortUrl("abc1234567"))
        assertTrue(urlShortenerService.isValidShortUrl("abc1234"))
    }

    @Test
    fun `should validate short url characters`() {
        // given & when & then
        assertTrue(urlShortenerService.isValidShortUrl("abcDEF1"))
        assertFalse(urlShortenerService.isValidShortUrl("abc#123"))
        assertFalse(urlShortenerService.isValidShortUrl("abc def"))
        assertFalse(urlShortenerService.isValidShortUrl("abc-123"))
    }

    @Test
    fun `should validate short url with mixed characters`() {
        // given & when & then
        assertTrue(urlShortenerService.isValidShortUrl("aB1cD2x"))
        assertTrue(urlShortenerService.isValidShortUrl("1234567"))
        assertTrue(urlShortenerService.isValidShortUrl("abcdefg"))
        assertTrue(urlShortenerService.isValidShortUrl("ABCDEFG"))
    }
}