package com.ayakashikitsune.oasis.data.jsonModels

import java.time.LocalDateTime

data class LoggerError(
    val time : LocalDateTime = LocalDateTime.now(),
    val message : String? = "",
    val fromFunction : String = "",
)
