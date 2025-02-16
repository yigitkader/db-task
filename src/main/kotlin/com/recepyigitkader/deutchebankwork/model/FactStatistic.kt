package com.recepyigitkader.deutchebankwork.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "fact_analytics")
data class FactStatistic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id", nullable = false)
    val fact: Fact,

    @Column(nullable = false)
    val firstAccessedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val lastAccessedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val accessCount: Long
) {
    constructor() : this(
        id = null,
        fact = Fact(),
        firstAccessedAt = LocalDateTime.now(),
        lastAccessedAt = LocalDateTime.now(),
        accessCount = 0
    )
}
