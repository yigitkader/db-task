package com.recepyigitkader.deutchebankwork.controller.publicapi

import com.recepyigitkader.deutchebankwork.dto.FactResponse
import com.recepyigitkader.deutchebankwork.service.FactService
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/v1/facts")
class FactsController(
    private val factService: FactService
) {
    @PostMapping()
    fun fetchSaveShorter(): FactResponse {
        return factService.fetchFact()
    }

    @GetMapping("/{shortenedUrl}")
    fun getFact(@PathVariable(value = "shortenedUrl", required = false) shortenedUrl: String): FactResponse? {
        return factService.getFact(shortenedUrl)
    }

    @GetMapping
    fun getFacts(): List<FactResponse> {
        return factService.getFacts()
    }
}