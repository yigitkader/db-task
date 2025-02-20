package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import com.recepyigitkader.deutchebankwork.dto.FactClientResponse
import com.recepyigitkader.deutchebankwork.dto.FactResponse
import com.recepyigitkader.deutchebankwork.exceptions.ExternalCallException
import com.recepyigitkader.deutchebankwork.exceptions.UnableToUrlShorteningException
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Service
class FactService(
    private val webClient: WebClient,
    private val factsConfig: FactsConfig,
    private val statisticService: StatisticService,
    private val factRepository: FactRepository,
    private val urlShortenerService: UrlShortenerService,
    private val factCacheService: FactCacheService,
) {

    private val logger = LoggerFactory.getLogger(FactService::class.java)

    @CacheEvict(cacheNames = ["facts", "facts_all"], allEntries = true)
    @Transactional
    fun fetchFact(): ResponseEntity<Any> {
        val externalFact = callExternalAPI()
            ?: throw ExternalCallException("Failed to fetch fact from external API")

        factCacheService.getFactByOriginalFact(externalFact.permalink)?.let {
            statisticService.incrementAccessCountFactAnalytic(it)
            return ResponseEntity.ok(it.toResponse())
        }

        val shortUrl = urlShortenerService.generateShortUrl()

        try {
            val fact = Fact(
                originalFact = externalFact.permalink,
                shortenedUrl = shortUrl,
            )

            val saved = factRepository.save(fact)
            logger.info("New fact added: $saved")

            val statistic = statisticService.addStatistic(fact = saved)
            logger.info("New statistic added: $statistic")

            return ResponseEntity.ok(saved.toResponse())

        } catch (e: Exception) {
            throw UnableToUrlShorteningException("Failed to save fact")
        }
    }


    private fun callExternalAPI(): FactClientResponse? {
        try {
            val url = factsConfig.api

            val response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(FactClientResponse::class.java)
                .block()
            return response
        } catch (e: Exception) {
            logger.error("Error calling external API", e)
            throw ExternalCallException("Unexpected exception while fetching fact from external API, ${e.message}")
        }
    }

    fun getFact(shortenedUrl: String): ResponseEntity<Any> {
        val fact = factCacheService.getFactByShortenedURL(shortenedUrl) ?: return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to "Fact not found"))

        statisticService.incrementAccessCountFactAnalytic(fact)

        return ResponseEntity.ok(fact.toResponse())
    }

    @Cacheable(cacheNames = ["facts_all"])
    fun getFacts(): ResponseEntity<Any> {
        return ResponseEntity.ok(factRepository.findAll().map { it.toResponse() })
    }

    fun getFactRedirect(shortenedUrl: String): ResponseEntity<Any> {
        return factCacheService.getFactByShortenedURL(shortenedUrl)?.let {
            statisticService.incrementAccessCountFactAnalytic(it)
            ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(it.originalFact))
                .build()
        } ?: ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to "Fact not found with shortened URL: $shortenedUrl"))
    }

    private fun Fact.toResponse() = FactResponse(
        originalFact = originalFact,
        shortenedUrl = shortenedUrl,
    )
}
