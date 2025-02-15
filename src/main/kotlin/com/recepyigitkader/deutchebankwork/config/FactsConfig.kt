package com.recepyigitkader.deutchebankwork.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "facts")
class FactsConfig {
    lateinit var api: String
    var secrets: List<String> = emptyList()
}