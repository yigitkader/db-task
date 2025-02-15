package com.recepyigitkader.deutchebankwork.controller.privateapi

import com.recepyigitkader.deutchebankwork.dto.CacheStats
import com.recepyigitkader.deutchebankwork.service.CacheService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/admin/statistics/cache")
class CacheController(
    private val cacheService: CacheService
) {

    @GetMapping
    fun getCacheStats(): Map<String, CacheStats> {
        return cacheService.getCacheStats()
    }
}