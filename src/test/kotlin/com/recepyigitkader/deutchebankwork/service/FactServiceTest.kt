package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import com.recepyigitkader.deutchebankwork.dto.FactClientResponse
import com.recepyigitkader.deutchebankwork.exceptions.ExternalCallException
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
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
    private lateinit var timeService: TimeService

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var factService: FactService

    companion object {
        private val NOW = LocalDateTime.MIN

        private val factClientResponse = FactClientResponse(
            id = "factId",
            text = "Test fact",
            source = "Test Source",
            sourceUrl = "http://test.com",
            language = "en",
            permalink = "http://test.com/fact"
        )

        private val fact = Fact(
            factId = "factId",
            text = "Test fact",
            source = "Test Source",
            sourceUrl = "http://test.com",
            language = "en",
            permalink = "http://test.com/fact",
            shortenedUrl = "abc123",
            createdDate = NOW
        )

        private val factStatistic = FactStatistic(
            fact = fact,
            accessedAt = NOW
        )
    }

    @Test
    fun `should fetch and save new fact`() {
        // given
        setupWebClientMock()
        `when`(timeService.getLocalDateTime()).thenReturn(NOW)
        `when`(factRepository.findByPermalink(factClientResponse.permalink)).thenReturn(null)
        `when`(urlShortenerService.generateShortUrl()).thenReturn("abc123")
        `when`(factRepository.save(fact)).thenReturn(fact)
        `when`(statisticService.addStatistic(fact)).thenReturn(factStatistic)

        // when
        val result = factService.fetchFact()

        // then
        assertEquals(fact.text, result.text)
        assertEquals(fact.shortenedUrl, result.shortenedUrl)

        verify(factRepository).findByPermalink(factClientResponse.permalink)
        verify(urlShortenerService).generateShortUrl()
        verify(factRepository).save(fact)
        verify(statisticService).addStatistic(fact)
        verify(timeService).getLocalDateTime()
    }

    @Test
    fun `should return existing fact if already exists`() {
        // given
        setupWebClientMock()
        `when`(factRepository.findByPermalink(factClientResponse.permalink)).thenReturn(fact)

        // when
        val result = factService.fetchFact()

        // then
        assertEquals(fact.text, result.text)
        assertEquals(fact.shortenedUrl, result.shortenedUrl)

        verify(factRepository).findByPermalink(factClientResponse.permalink)
        verify(urlShortenerService, never()).generateShortUrl()
        verify(factRepository, never()).save(fact)
        verify(statisticService, never()).addStatistic(fact)
    }

    @Test
    fun `should get fact by shortened url`() {
        // given
        `when`(factRepository.findByShortenedUrl("abc123")).thenReturn(fact)

        // when
        val result = factService.getFact("abc123")

        // then
        assertNotNull(result)
        assertEquals(fact.text, result?.text)
        assertEquals(fact.shortenedUrl, result?.shortenedUrl)
        verify(factRepository).findByShortenedUrl("abc123")
    }

    @Test
    fun `should return null when fact not found by shortened url`() {
        // given
        `when`(factRepository.findByShortenedUrl("notfound")).thenReturn(null)

        // when
        val result = factService.getFact("notfound")

        // then
        assertNull(result)
        verify(factRepository).findByShortenedUrl("notfound")
    }

    @Test
    fun `should get all facts`() {
        // given
        val facts = listOf(fact)
        `when`(factRepository.findAll()).thenReturn(facts)

        // when
        val results = factService.getFacts()

        // then
        assertEquals(1, results.size)
        assertEquals(facts[0].text, results[0].text)
        assertEquals(facts[0].shortenedUrl, results[0].shortenedUrl)
        verify(factRepository).findAll()
    }

    @Test
    fun `should throw exception when external api call fails`() {
        // given
        setupWebClientErrorMock()

        // when & then
        assertThrows<ExternalCallException> {
            factService.fetchFact()
        }

        verify(factRepository, never()).save(fact)
        verify(statisticService, never()).addStatistic(fact)
    }

    private fun setupWebClientMock() {
        `when`(factsConfig.api).thenReturn("test-url")
        `when`(webClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("test-url")).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.bodyToMono(FactClientResponse::class.java)).thenReturn(Mono.just(factClientResponse))
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