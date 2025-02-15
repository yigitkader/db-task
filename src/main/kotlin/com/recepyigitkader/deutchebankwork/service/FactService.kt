package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import com.recepyigitkader.deutchebankwork.dto.FactClientResponse
import com.recepyigitkader.deutchebankwork.dto.FactResponse
import com.recepyigitkader.deutchebankwork.exceptions.ExternalCallException
import com.recepyigitkader.deutchebankwork.exceptions.UnexpectedException
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient

@Service
class FactService(
    private val webClient: WebClient,
    private val factsConfig: FactsConfig,
    private val statisticService: StatisticService,
    private val factRepository: FactRepository,
    private val urlShortenerService: UrlShortenerService,
    private val timeService: TimeService,
) {

    private val logger = LoggerFactory.getLogger(FactService::class.java)

    @Transactional
    fun fetchFact(): FactResponse {
        val externalFact = callExternalAPI()
            ?: throw ExternalCallException("Failed to fetch fact from external API")

        factRepository.findByPermalink(externalFact.permalink)?.let {
            return it.toResponse()
        }

        val shortUrl = urlShortenerService.generateShortUrl()

        try {
            val fact = Fact(
                factId = externalFact.id,
                text = externalFact.text,
                source = externalFact.source,
                sourceUrl = externalFact.sourceUrl,
                language = externalFact.language,
                permalink = externalFact.permalink,
                shortenedUrl = shortUrl,
                createdDate = timeService.getLocalDateTime(),
            )

            val saved = factRepository.save(fact)
            logger.info("New fact: $saved")
            val statistic = statisticService.addStatistic(fact = fact)
            logger.info("New statistic added: $statistic")

            return saved.toResponse()

        } catch (e: Exception) {
            throw UnexpectedException("Failed to save fact")
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
            throw ExternalCallException("Unexpected exception while fetching fact from external API, ${e.message}")
        }
    }

    @Cacheable(cacheNames = ["facts"], key = "#shortenedUrl")
    fun getFact(shortenedUrl: String): FactResponse? {
        return factRepository.findByShortenedUrl(shortenedUrl)?.toResponse()
    }

    @Cacheable(cacheNames = ["facts_all"])
    fun getFacts(): List<FactResponse> {
        return factRepository.findAll().map { it.toResponse() }
    }


    private fun Fact.toResponse() = FactResponse(
        factId = factId,
        text = text,
        source = source,
        sourceUrl = sourceUrl,
        language = language,
        permalink = permalink,
        shortenedUrl = shortenedUrl,
        createdDate = createdDate
    )
}
