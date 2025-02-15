package com.recepyigitkader.deutchebankwork.service

import com.recepyigitkader.deutchebankwork.repository.FactRepository
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class UrlShortenerService(private val factRepository: FactRepository) {

    private val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val random = SecureRandom()
    private val urlLength = 7


    fun generateShortUrl(): String {
        var shortUrl: String
        do {
            shortUrl = buildString {
                repeat(urlLength) {
                    append(chars[random.nextInt(chars.length)])
                }
            }
        } while (factRepository.existsFactByShortenedUrl(shortUrl))

        return shortUrl
    }

    fun isValidShortUrl(shortUrl: String): Boolean {
        if (shortUrl.length != urlLength) return false
        return shortUrl.all { it in chars }
    }
}