package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactAnalytic
import com.recepyigitkader.deutchebankwork.repository.FactAnalyticRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AnalyticsService(
    private val factAnalyticRepository: FactAnalyticRepository,
) {
    @CacheEvict(cacheNames = ["facts"], key = "#fact.shortenedUrl")
    fun addStatistic(fact: Fact) {
        factAnalyticRepository.save(FactAnalytic(fact = fact, accessedAt = LocalDateTime.now()))
    }


    @Cacheable(cacheNames = ["analytics"])
    fun getAllAnalytics(): List<AnalyticsResponse> {
        return factAnalyticRepository.findAll().map { AnalyticsResponse(it.fact, it.accessedAt) }
    }
}