package com.pedroaba.tccmobile.game.visual

object DistanceVisualMapper {
    fun runnerX(
        distance: Double,
        goalDistance: Double,
        minX: Double,
        maxX: Double
    ): Double {
        if (goalDistance <= 0.0) return minX

        val progress = (distance / goalDistance).coerceIn(0.0, 1.0)
        return minX + (maxX - minX) * progress
    }
}
