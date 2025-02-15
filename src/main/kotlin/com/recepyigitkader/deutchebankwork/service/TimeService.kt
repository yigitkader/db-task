package com.recepyigitkader.deutchebankwork.service

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TimeService {

    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}