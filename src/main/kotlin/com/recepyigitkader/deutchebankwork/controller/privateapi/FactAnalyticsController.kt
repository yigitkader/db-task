package com.recepyigitkader.deutchebankwork.controller.privateapi

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.service.StatisticService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/admin/statistics")
class FactAnalyticsController(
    private val statisticService: StatisticService
) {
    @GetMapping()
    fun getAnalytics(): List<AnalyticsResponse> {
        return statisticService.getAllAnalytics()
    }
}