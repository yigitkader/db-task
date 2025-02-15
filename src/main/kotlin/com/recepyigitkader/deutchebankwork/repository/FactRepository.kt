package com.recepyigitkader.deutchebankwork.repository

import com.recepyigitkader.deutchebankwork.model.Fact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FactRepository : JpaRepository<Fact, Long> {
    fun existsFactByShortenedUrl(shortenedUrl: String): Boolean
    fun findByShortenedUrl(shortenedUrl: String): Fact?
    fun findByPermalink(permalink: String): Fact?
}