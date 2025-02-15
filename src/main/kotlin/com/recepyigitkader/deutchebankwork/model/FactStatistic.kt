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
    val accessedAt: LocalDateTime = LocalDateTime.now(),

) {
    constructor() : this(
        id = null,
        fact = Fact(),
        accessedAt = LocalDateTime.now(),
    )
}
