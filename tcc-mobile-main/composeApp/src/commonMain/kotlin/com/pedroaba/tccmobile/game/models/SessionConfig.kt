package com.pedroaba.tccmobile.game.models

data class SessionConfig(
    val goalDistance: Double = 1000.0,
    val sessionDurationSeconds: Double = 60.0,
    val chaseRatePerSecond: Double = 24.0,
    val escapeRatePerSecond: Double = 18.0,
    val initialDistance: Double = 990.0,
    val targetMinBpm: Int = 108,
    val targetMaxBpm: Int = 135,
    val maxHeartRate: Int = 180,
    val cadenceMin: Int = 160,
    val cadenceMax: Int = 180
)
