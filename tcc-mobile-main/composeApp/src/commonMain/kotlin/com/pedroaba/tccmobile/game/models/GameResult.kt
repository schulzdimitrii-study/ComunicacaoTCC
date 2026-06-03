package com.pedroaba.tccmobile.game.models

data class GameResult(
    val status: String,
    val totalTime: Double,
    val totalDistance: Double,
    val averageBpm: Double,
    val averageCadence: Double
)
