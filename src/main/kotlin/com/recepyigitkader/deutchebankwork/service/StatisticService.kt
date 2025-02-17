package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.dto.FactResponse
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactStatisticRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class StatisticService(
    private val factStatisticRepository: FactStatisticRepository,
    private val timeService: TimeService
) {

    @CacheEvict(cacheNames = ["analytics"])
    fun addStatistic(fact: Fact): FactStatistic {
        return factStatisticRepository.save(
            FactStatistic(
                fact = fact,
                firstAccessedAt = timeService.getLocalDateTime(),
                lastAccessedAt = timeService.getLocalDateTime(),
                accessCount = 1
            )
        )
    }

    @CacheEvict(cacheNames = ["facts", "facts_all", "analytics"], allEntries = true)
    fun incrementAccessCountFactAnalytic(fact: Fact) {
        factStatisticRepository.findByFact(fact)?.let {
            factStatisticRepository.save(
                it.copy(
                    firstAccessedAt = it.firstAccessedAt,
                    lastAccessedAt = timeService.getLocalDateTime(),
                    accessCount = it.accessCount + 1
                )
            )
        }
    }

    @Cacheable(cacheNames = ["analytics"])
    fun getAllAnalytics(): List<AnalyticsResponse> {
        return factStatisticRepository.findAll()
            .map {
                AnalyticsResponse(
                    fact = FactResponse(originalFact = it.fact.originalFact, shortenedUrl = it.fact.shortenedUrl),
                    firstAccessedAt = it.firstAccessedAt,
                    lastAccessedAt = it.lastAccessedAt,
                    accessCount = it.accessCount
                )
            }
    }

}