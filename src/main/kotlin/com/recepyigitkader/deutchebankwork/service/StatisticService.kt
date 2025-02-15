package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactStatisticRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class StatisticService(
    private val factStatisticRepository: FactStatisticRepository,
) {
    @CacheEvict(cacheNames = ["facts"], key = "#fact.shortenedUrl")
    fun addStatistic(fact: Fact): FactStatistic {
        return factStatisticRepository.save(FactStatistic(fact = fact, accessedAt = fact.createdDate))
    }


    @Cacheable(cacheNames = ["analytics"])
    fun getAllAnalytics(): List<AnalyticsResponse> {
        return factStatisticRepository.findAll().map { AnalyticsResponse(it.fact, it.accessedAt) }
    }
}