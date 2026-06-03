package com.pedroaba.tccmobile.backend.online

import com.pedroaba.tccmobile.backend.model.HordeDto
import com.pedroaba.tccmobile.game.models.SessionConfig
import kotlin.math.round

fun HordeDto.toSessionConfig(): SessionConfig {
    val baseConfig = SessionConfig()
    val difficultyMultiplier = when (difficulty.uppercase()) {
        "EASY" -> 0.85
        "HARD" -> 1.2
        else -> 1.0
    }
    val paceMultiplier = targetPace
        ?.takeIf { it > 0.0 }
        ?.let { (6.0 / it).coerceIn(0.75, 1.35) }
        ?: 1.0

    return baseConfig.copy(
        sessionDurationSeconds = estimatedDuration
            .takeIf { it > 0 }
            ?.let { it * 60.0 }
            ?: baseConfig.sessionDurationSeconds,
        chaseRatePerSecond = roundToTwoDecimals(baseConfig.chaseRatePerSecond * difficultyMultiplier * paceMultiplier)
    )
}

fun HordeDto.displayDifficulty(): String = when (difficulty.uppercase()) {
    "EASY" -> "Fácil"
    "HARD" -> "Difícil"
    else -> "Média"
}

fun HordeDto.displayPace(): String = targetPace
    ?.takeIf { it > 0.0 }
    ?.let { "${roundToOneDecimal(it)} min/km" }
    ?: "sem pace alvo"

fun HordeDto.displayDuration(): String = estimatedDuration
    .takeIf { it > 0 }
    ?.let { "$it min" }
    ?: "duracao livre"

private fun roundToOneDecimal(value: Double): Double = round(value * 10.0) / 10.0

private fun roundToTwoDecimals(value: Double): Double = round(value * 100.0) / 100.0
