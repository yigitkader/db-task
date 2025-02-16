package com.recepyigitkader.deutchebankwork.model

import jakarta.persistence.*

@Entity
@Table(name = "facts")
data class Fact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val originalFact: String,

    @Column(nullable = false, unique = true)
    val shortenedUrl: String,
) {
    constructor() : this(
        originalFact = "",
        shortenedUrl = "",
    )
}
