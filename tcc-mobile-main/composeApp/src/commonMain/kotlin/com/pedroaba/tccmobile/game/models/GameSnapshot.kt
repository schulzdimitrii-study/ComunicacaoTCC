package com.pedroaba.tccmobile.game.models

data class GameSnapshot(
    val distance: Double = 0.0,
    val hordePressure: Double = 1.0,
    val risk: Double = 1.0,
    val performanceScore: Double = 0.0,
    val runnerVelocity: Double = 0.0,
    val hordeVelocity: Double = 0.0,
    val elapsedSeconds: Double = 0.0,
    val result: String = "running"
)
