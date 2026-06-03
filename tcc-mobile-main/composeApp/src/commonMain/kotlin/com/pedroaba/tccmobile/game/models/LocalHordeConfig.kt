package com.pedroaba.tccmobile.game.models

import kotlin.math.round

enum class LocalHordeDifficulty(
    val label: String,
    val chaseMultiplier: Double
) {
    EASY("Fácil", 0.85),
    MEDIUM("Média", 1.0),
    HARD("Difícil", 1.2)
}

data class LocalHordeConfig(
    val distanceKm: Double = 1.0,
    val difficulty: LocalHordeDifficulty = LocalHordeDifficulty.MEDIUM
)

fun LocalHordeConfig.toSessionConfig(): SessionConfig {
    val baseConfig = SessionConfig()
    val distanceMeters = (distanceKm.takeIf { it > 0.0 } ?: 1.0) * 1_000.0

    return baseConfig.copy(
        goalDistance = roundToTwoDecimals(distanceMeters),
        initialDistance = roundToTwoDecimals((distanceMeters * 0.35).coerceAtLeast(120.0)),
        chaseRatePerSecond = roundToTwoDecimals(baseConfig.chaseRatePerSecond * difficulty.chaseMultiplier)
    )
}

private fun roundToTwoDecimals(value: Double): Double = round(value * 100.0) / 100.0
