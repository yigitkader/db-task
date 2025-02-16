package com.recepyigitkader.deutchebankwork.controller.publicapi

import com.recepyigitkader.deutchebankwork.service.FactService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class FactsController(
    private val factService: FactService
) {
    @PostMapping("/facts")
    fun fetchSaveShorter(): ResponseEntity<Any> {
        return factService.fetchFact()
    }

    @GetMapping("/facts/{shortenedUrl}")
    fun getFact(
        @PathVariable(
            value = "shortenedUrl",
            required = false
        ) shortenedUrl: String
    ): ResponseEntity<Any> {
        return factService.getFact(shortenedUrl)
    }

    @GetMapping("/facts/{shortenedUrl}/redirect")
    fun redirect(@PathVariable(value = "shortenedUrl", required = false) shortenedUrl: String): ResponseEntity<Any> {
        return factService.getFactRedirect(shortenedUrl)
    }

    @GetMapping("/facts")
    fun getFacts(): ResponseEntity<Any> {
        return factService.getFacts()
    }
}