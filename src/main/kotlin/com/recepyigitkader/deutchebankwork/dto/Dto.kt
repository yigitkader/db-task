package com.recepyigitkader.deutchebankwork.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class FactClientResponse(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("text")
    val text: String,

    @JsonProperty("source")
    val source: String,

    @JsonProperty("source_url")
    val sourceUrl: String,

    @JsonProperty("language")
    val language: String,

    @JsonProperty("permalink")
    val permalink: String
) {
    constructor() : this("", "", "", "", "", "") // necessary for ObjectMapper
}

data class AnalyticsResponse(
    val fact: FactResponse,
    val firstAccessedAt: LocalDateTime,
    val lastAccessedAt: LocalDateTime,
    val accessCount: Long
)

data class FactResponse(
    val originalFact: String,
    val shortenedUrl: String,
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