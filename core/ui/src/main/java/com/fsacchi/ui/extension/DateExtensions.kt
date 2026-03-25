package com.fsacchi.ui.extension

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zoneDefault get() = ZoneId.systemDefault()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochSecond(this).atZone(zoneDefault).toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this), zoneDefault)

fun Long.millisToLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zoneDefault)

fun Long.toTimeHHmm(): String =
    toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))

fun Long.toForecastDateTime(): String =
    toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))

fun Long.toHistoryDateTime(): String =
    millisToLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
