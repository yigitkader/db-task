package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactStatisticRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class StatisticServiceTest {

    @Mock
    private lateinit var factStatisticRepository: FactStatisticRepository

    @InjectMocks
    private lateinit var statisticService: StatisticService

    companion object {
        private val NOW = LocalDateTime.now()

        private fun createFact() = Fact(
            factId = "factId",
            text = "Sample fact text",
            source = "Test Source",
            sourceUrl = "http://test.com",
            language = "en",
            permalink = "http://test.com/fact",
            shortenedUrl = "abc123",
            createdDate = NOW
        )

        private fun createFactAnalytic(fact: Fact) = FactStatistic(
            fact = fact,
            accessedAt = NOW,
        )

        private fun createAnalyticsResponse(fact: Fact) = AnalyticsResponse(
            fact = fact,
            accessedAt = NOW
        )
    }

    @Test
    fun `should save analytic successfully`() {
        // Given
        val fact = createFact()
        val factAnalytic = createFactAnalytic(fact)
        `when`(factStatisticRepository.save(factAnalytic)).thenReturn(factAnalytic)

        // When
        val result = statisticService.addStatistic(fact)

        // Then
        verify(factStatisticRepository).save(factAnalytic)
        assertEquals(factAnalytic, result)
    }

    @Test
    fun `should get all analytics`() {
        // Given
        val fact = createFact()
        val factAnalytic = createFactAnalytic(fact)
        val analyticsResponse = createAnalyticsResponse(fact)
        `when`(factStatisticRepository.findAll()).thenReturn(listOf(factAnalytic))

        // When
        val result = statisticService.getAllAnalytics()

        // Then
        assertEquals(listOf(analyticsResponse), result)
        verify(factStatisticRepository).findAll()
    }

    @Test
    fun `should return empty list when no analytics exist`() {
        // Given
        `when`(factStatisticRepository.findAll()).thenReturn(emptyList())

        // When
        val result = statisticService.getAllAnalytics()

        // Then
        assertTrue(result.isEmpty())
        verify(factStatisticRepository).findAll()
    }
}