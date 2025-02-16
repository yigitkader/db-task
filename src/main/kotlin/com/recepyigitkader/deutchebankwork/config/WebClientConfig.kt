package com.recepyigitkader.deutchebankwork.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
                configurer.defaultCodecs().jackson2JsonDecoder(
                    Jackson2JsonDecoder(
                        ObjectMapper().apply {
                            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        }
                    )
                )
            }
            .defaultHeaders { headers ->
                headers.accept = listOf(MediaType.APPLICATION_JSON)
                headers.contentType = MediaType.APPLICATION_JSON
            }
            .build()
    }
}
