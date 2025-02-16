package com.recepyigitkader.deutchebankwork.repository

import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import org.springframework.data.jpa.repository.JpaRepository

interface FactStatisticRepository : JpaRepository<FactStatistic, Long> {
    fun findByFact(fact: Fact): FactStatistic?
}