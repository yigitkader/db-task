package com.recepyigitkader.deutchebankwork.controller.privateapi

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.service.AnalyticsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/v1/admin/analytics")
class FactAnalyticsController(
    private val analyticsService: AnalyticsService
) {
    @GetMapping()
    fun getAnalytics(): List<AnalyticsResponse> {
        return analyticsService.getAllAnalytics()
    }
}