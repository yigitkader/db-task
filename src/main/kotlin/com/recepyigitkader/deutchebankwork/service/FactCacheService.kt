package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class FactCacheService(
    private val factRepository: FactRepository,
) {
    @Cacheable(cacheNames = ["facts"], key = "#shortenedUrl")
    fun getFactByShortenedURL(shortenedUrl: String): Fact? {
        return factRepository.findByShortenedUrl(shortenedUrl)
    }

    @Cacheable(cacheNames = ["facts"], key = "#originalFact")
    fun getFactByOriginalFact(originalFact: String): Fact? {
        return factRepository.findByOriginalFact(originalFact)
    }
}
