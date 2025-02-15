package com.recepyigitkader.deutchebankwork

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * Created by RecepYigitKader
 */
@SpringBootApplication
@EnableConfigurationProperties(FactsConfig::class)
class DeutcheBankWorkApplication

fun main(args: Array<String>) {
    runApplication<DeutcheBankWorkApplication>(*args)
}
