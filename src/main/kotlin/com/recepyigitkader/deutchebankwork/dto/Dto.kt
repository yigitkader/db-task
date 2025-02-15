package com.recepyigitkader.deutchebankwork.dto

import com.recepyigitkader.deutchebankwork.model.Fact
import java.time.LocalDateTime

data class FactClientResponse(
    val id: String,
    val text: String,
    val source: String,
    val sourceUrl: String,
    val language: String,
    val permalink: String
)

data class FactResponse(
    val text: String,
    val source: String,
    val sourceUrl: String,
    val language: String,
    val permalink: String,
    val shortenedUrl: String,
    val createdDate: LocalDateTime,
)

data class AnalyticsResponse(
    val fact: Fact,
    val accessedAt: LocalDateTime
)


data class CacheStats(
    val cacheName: String,
    val size: Int,
    val entries: List<CacheEntry>
)

data class CacheEntry(
    val key: String,
    val value: String
)