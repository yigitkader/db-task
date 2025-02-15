package com.recepyigitkader.deutchebankwork.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "facts")
data class Fact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val text: String,

    @Column(nullable = false)
    val source: String,

    @Column(nullable = false)
    val sourceUrl: String,

    @Column(nullable = false)
    val language: String,

    @Column(nullable = false, unique = true)
    val permalink: String,

    @Column(nullable = false, unique = true)
    val shortenedUrl: String,

    @Column(nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        id = null,
        text = "",
        source = "",
        sourceUrl = "",
        language = "",
        permalink = "",
        shortenedUrl = "",
        createdDate = LocalDateTime.now()
    )
}
