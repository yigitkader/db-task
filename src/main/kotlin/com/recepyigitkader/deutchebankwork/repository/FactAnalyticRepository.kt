package com.recepyigitkader.deutchebankwork.repository

import com.recepyigitkader.deutchebankwork.model.FactAnalytic
import org.springframework.data.jpa.repository.JpaRepository

interface FactAnalyticRepository : JpaRepository<FactAnalytic, Long> {
}