package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.dto.AnalyticsResponse
import com.recepyigitkader.deutchebankwork.dto.FactResponse

import com.recepyigitkader.deutchebankwork.model.Fact
import com.recepyigitkader.deutchebankwork.model.FactStatistic
import com.recepyigitkader.deutchebankwork.repository.FactStatisticRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class StatisticServiceTest {

    @Mock
    private lateinit var factStatisticRepository: FactStatisticRepository

    @Mock
    private lateinit var timeService: TimeService

    @InjectMocks
    private lateinit var statisticService: StatisticService

    companion object {
        private val NOW = LocalDateTime.now()

        private val FACT = Fact(
            id = 1L,
            originalFact = "http://test.com/fact",
            shortenedUrl = "abc123"
        )

        private val FACT_STATISTIC = FactStatistic(
            id = null,
            fact = FACT,
            firstAccessedAt = NOW,
            lastAccessedAt = NOW,
            accessCount = 1
        )

        private val ANALYTICS_RESPONSE = AnalyticsResponse(
            fact = FactResponse(
                originalFact = FACT.originalFact,
                shortenedUrl = FACT.shortenedUrl
            ),
            firstAccessedAt = NOW,
            lastAccessedAt = NOW,
            accessCount = 1
        )
    }

    @Test
    fun `should add new statistic`() {
        // given
        `when`(timeService.getLocalDateTime()).thenReturn(NOW)
        `when`(factStatisticRepository.save(FACT_STATISTIC)).thenReturn(FACT_STATISTIC)

        // when
        val result = statisticService.addStatistic(FACT)

        // then
        assertEquals(FACT_STATISTIC, result)
        verify(factStatisticRepository).save(FACT_STATISTIC)
        verify(timeService, times(2)).getLocalDateTime()
    }

    @Test
    fun `should increment access count`() {
        // given
        val updatedStatistic = FACT_STATISTIC.copy(
            lastAccessedAt = NOW,
            accessCount = 2
        )

        val statisticCaptor = ArgumentCaptor.forClass(FactStatistic::class.java)

        `when`(timeService.getLocalDateTime()).thenReturn(NOW)
        `when`(factStatisticRepository.findByFact(FACT)).thenReturn(FACT_STATISTIC)
        `when`(factStatisticRepository.save(any())).thenReturn(updatedStatistic)

        // when
        statisticService.incrementAccessCountFactAnalytic(FACT)

        // then
        verify(factStatisticRepository).findByFact(FACT)
        verify(factStatisticRepository).save(statisticCaptor.capture())

        val savedStatistic = statisticCaptor.value
        assertEquals(2, savedStatistic.accessCount)
        assertEquals(NOW, savedStatistic.lastAccessedAt)
        assertEquals(FACT_STATISTIC.firstAccessedAt, savedStatistic.firstAccessedAt)

        verify(timeService).getLocalDateTime()
    }

    @Test
    fun `should not increment access count when statistic not found`() {
        // given
        `when`(factStatisticRepository.findByFact(FACT)).thenReturn(null)

        // when
        statisticService.incrementAccessCountFactAnalytic(FACT)

        // then
        verify(factStatisticRepository).findByFact(FACT)
        verify(factStatisticRepository, never()).save(any())
        verify(timeService, never()).getLocalDateTime()
    }

    @Test
    fun `should get all analytics`() {
        // given
        `when`(factStatisticRepository.findAll()).thenReturn(listOf(FACT_STATISTIC))

        // when
        val result = statisticService.getAllAnalytics()

        // then
        assertEquals(1, result.size)
        assertEquals(ANALYTICS_RESPONSE, result[0])
        verify(factStatisticRepository).findAll()
    }

    @Test
    fun `should return empty list when no analytics found`() {
        // given
        `when`(factStatisticRepository.findAll()).thenReturn(emptyList())

        // when
        val result = statisticService.getAllAnalytics()

        // then
        assertTrue(result.isEmpty())
        verify(factStatisticRepository).findAll()
    }
}