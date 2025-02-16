package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import com.recepyigitkader.deutchebankwork.dto.FactClientResponse
import com.recepyigitkader.deutchebankwork.dto.FactResponse
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class FactServiceTest {

    @Mock
    private lateinit var webClient: WebClient

    @Mock
    private lateinit var factsConfig: FactsConfig

    @Mock
    private lateinit var statisticService: StatisticService

    @Mock
    private lateinit var factRepository: FactRepository

    @Mock
    private lateinit var urlShortenerService: UrlShortenerService

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var factService: FactService

    companion object {
        private val FACT_CLIENT_RESPONSE = FactClientResponse(
            id = "factId",
            text = "Test fact",
            source = "Test Source",
            sourceUrl = "http://test.com",
            language = "en",
            permalink = "http://test.com/fact"
        )

        private val FACT = Fact(
            id = null,
            originalFact = "http://test.com/fact",
            shortenedUrl = "abc123"
        )

        private val SAVED_FACT = Fact(
            id = 1,
            originalFact = "http://test.com/fact",
            shortenedUrl = "abc123"
        )

        private val FACT_STATISTIC = FactStatistic(1, SAVED_FACT, LocalDateTime.now(), LocalDateTime.now(), 1)

        private val FACT_RESPONSE = FactResponse(
            originalFact = "http://test.com/fact",
            shortenedUrl = "abc123"
        )

        private val SHORTENED_URL = "abc123"
    }

    @Test
    fun `should fetch and save new fact`() {
        // given
        setupWebClientMock()
        `when`(factRepository.findByOriginalFact(FACT_CLIENT_RESPONSE.permalink)).thenReturn(null)
        `when`(urlShortenerService.generateShortUrl()).thenReturn(SHORTENED_URL)
        `when`(factRepository.save(FACT)).thenReturn(SAVED_FACT)
        `when`(statisticService.addStatistic(SAVED_FACT)).thenReturn(FACT_STATISTIC)

        // when
        val result = factService.fetchFact()

        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val responseBody = result.body as FactResponse
        assertEquals(FACT_RESPONSE.originalFact, responseBody.originalFact)
        assertEquals(FACT_RESPONSE.shortenedUrl, responseBody.shortenedUrl)

        verify(factRepository).findByOriginalFact(FACT_CLIENT_RESPONSE.permalink)
        verify(urlShortenerService).generateShortUrl()
        verify(factRepository).save(FACT)
        verify(statisticService).addStatistic(SAVED_FACT)
    }

    @Test
    fun `should return existing fact and increment access count if already exists`() {
        // given
        setupWebClientMock()
        `when`(factRepository.findByOriginalFact(FACT_CLIENT_RESPONSE.permalink)).thenReturn(FACT)
        doNothing().`when`(statisticService).incrementAccessCountFactAnalytic(FACT)

        // when
        val result = factService.fetchFact()

        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val responseBody = result.body as FactResponse
        assertEquals(FACT_RESPONSE.originalFact, responseBody.originalFact)
        assertEquals(FACT_RESPONSE.shortenedUrl, responseBody.shortenedUrl)

        verify(factRepository).findByOriginalFact(FACT_CLIENT_RESPONSE.permalink)
        verify(statisticService).incrementAccessCountFactAnalytic(FACT)
        verify(urlShortenerService, never()).generateShortUrl()
        verify(factRepository, never()).save(FACT)
        verify(statisticService, never()).addStatistic(SAVED_FACT)
    }

    @Test
    fun `should get fact by shortened url and increment access count`() {
        // given
        `when`(factRepository.findByShortenedUrl("abc123")).thenReturn(FACT)
        doNothing().`when`(statisticService).incrementAccessCountFactAnalytic(FACT)

        // when
        val result = factService.getFact("abc123")

        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val responseBody = result.body as FactResponse
        assertEquals(FACT_RESPONSE.originalFact, responseBody.originalFact)
        assertEquals(FACT_RESPONSE.shortenedUrl, responseBody.shortenedUrl)
        verify(factRepository).findByShortenedUrl("abc123")
        verify(statisticService).incrementAccessCountFactAnalytic(FACT)
    }

    @Test
    fun `should return not found when fact not found by shortened url`() {
        // given
        `when`(factRepository.findByShortenedUrl("notfound")).thenReturn(null)

        // when
        val result = factService.getFact("notfound")

        // then
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        val responseBody = result.body as Map<*, *>
        assertTrue(responseBody["message"].toString().contains("not found"))
        verify(factRepository).findByShortenedUrl("notfound")
        verify(statisticService, never()).incrementAccessCountFactAnalytic(FACT)
    }

    @Test
    fun `should get all facts`() {
        // given
        val facts = listOf(FACT)
        `when`(factRepository.findAll()).thenReturn(facts)

        // when
        val result = factService.getFacts()

        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val responseBody = result.body as List<*>
        assertEquals(1, responseBody.size)
        val factResponse = responseBody[0] as FactResponse
        assertEquals(FACT_RESPONSE.originalFact, factResponse.originalFact)
        assertEquals(FACT_RESPONSE.shortenedUrl, factResponse.shortenedUrl)
        verify(factRepository).findAll()
    }

    @Test
    fun `should handle redirect for existing fact`() {
        // given
        `when`(factRepository.findByShortenedUrl("abc123")).thenReturn(FACT)
        doNothing().`when`(statisticService).incrementAccessCountFactAnalytic(FACT)

        // when
        val result = factService.getFactRedirect("abc123")

        // then
        assertEquals(HttpStatus.FOUND, result.statusCode)
        assertEquals(URI.create(FACT.originalFact), result.headers.location)
        verify(factRepository).findByShortenedUrl("abc123")
        verify(statisticService).incrementAccessCountFactAnalytic(FACT)
    }

    @Test
    fun `should return not found for redirect when fact does not exist`() {
        // given
        `when`(factRepository.findByShortenedUrl("notfound")).thenReturn(null)

        // when
        val result = factService.getFactRedirect("notfound")

        // then
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        val responseBody = result.body as Map<*, *>
        assertTrue(responseBody["message"].toString().contains("not found"))
        verify(factRepository).findByShortenedUrl("notfound")
        verify(statisticService, never()).incrementAccessCountFactAnalytic(FACT)
    }

    private fun setupWebClientMock() {
        `when`(factsConfig.api).thenReturn("test-url")
        `when`(webClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("test-url")).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.bodyToMono(FactClientResponse::class.java))
            .thenReturn(Mono.just(FACT_CLIENT_RESPONSE))
    }

    private fun setupWebClientErrorMock() {
        `when`(factsConfig.api).thenReturn("test-url")
        `when`(webClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("test-url")).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.bodyToMono(FactClientResponse::class.java))
            .thenReturn(Mono.error(RuntimeException("API Error")))
    }
}