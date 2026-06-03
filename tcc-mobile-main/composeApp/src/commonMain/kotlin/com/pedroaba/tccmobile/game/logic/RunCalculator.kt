package com.pedroaba.tccmobile.game.logic

import com.pedroaba.tccmobile.game.models.BiofeedbackSample
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.models.SessionConfig
import com.pedroaba.tccmobile.game.telemetry.model.EscapeMetrics
import kotlin.math.max

class RunCalculator {
    fun calculateSnapshot(
        currentDistance: Double,
        elapsedSeconds: Double,
        deltaSeconds: Double,
        sample: BiofeedbackSample,
        config: SessionConfig
    ): GameSnapshot {
        val bpmAlignment = calculateAlignment(
            value = sample.bpm.toDouble(),
            minVal = config.targetMinBpm.toDouble(),
            maxVal = config.targetMaxBpm.toDouble(),
            rangeLow = 35.0,
            rangeHigh = 25.0
        )

        val cadenceAlignment = calculateAlignment(
            value = sample.cadence.toDouble(),
            minVal = config.cadenceMin.toDouble(),
            maxVal = config.cadenceMax.toDouble(),
            rangeLow = 30.0,
            rangeHigh = 20.0
        )

        val bpmStrain = 1.0 - bpmAlignment
        val performanceScore = (bpmAlignment * 0.7 + cadenceAlignment * 0.3 - bpmStrain * 0.25).coerceIn(0.0, 1.0)
        val hordePressure = ((1.0 - performanceScore) * 0.75 + bpmStrain * 0.25).coerceIn(0.0, 1.0)
        val risk = (hordePressure * 0.8 + bpmStrain * 0.2).coerceIn(0.0, 1.0)

        val runnerVelocity = config.escapeRatePerSecond * performanceScore
        val hordeVelocity = config.chaseRatePerSecond * hordePressure
        val distance = currentDistance + (runnerVelocity - hordeVelocity) * deltaSeconds

        val status = when {
            distance <= 0.0 -> "caught"
            distance >= config.goalDistance -> "escaped"
            else -> "running"
        }

        return GameSnapshot(
            distance = distance,
            hordePressure = hordePressure,
            risk = risk,
            performanceScore = performanceScore,
            runnerVelocity = runnerVelocity,
            hordeVelocity = hordeVelocity,
            elapsedSeconds = elapsedSeconds + deltaSeconds,
            result = status
        )
    }

    fun calculateSnapshotFromEscapeMetrics(
        currentDistance: Double,
        elapsedSeconds: Double,
        deltaSeconds: Double,
        metrics: EscapeMetrics,
        config: SessionConfig
    ): GameSnapshot {
        val performanceScore = metrics.finalScore.coerceIn(0.0, 1.0)
        val hordePressure = (
            (1.0 - performanceScore) * 0.75 +
                (1.0 - metrics.normalizedAcceleration) * 0.10 +
                (1.0 - metrics.normalizedSpeed) * 0.15
            ).coerceIn(0.0, 1.0)
        val risk = (
            hordePressure * 0.7 +
                (1.0 - metrics.normalizedSpeed) * 0.2 +
                (1.0 - metrics.normalizedDistance) * 0.1
            ).coerceIn(0.0, 1.0)

        val runnerVelocity = config.escapeRatePerSecond * performanceScore
        val hordeVelocity = config.chaseRatePerSecond * hordePressure
        val distance = currentDistance + (runnerVelocity - hordeVelocity) * deltaSeconds

        val status = when {
            distance <= 0.0 -> "caught"
            distance >= config.goalDistance -> "escaped"
            else -> "running"
        }

        return GameSnapshot(
            distance = distance,
            hordePressure = hordePressure,
            risk = risk,
            performanceScore = performanceScore,
            runnerVelocity = runnerVelocity,
            hordeVelocity = hordeVelocity,
            elapsedSeconds = elapsedSeconds + deltaSeconds,
            result = status
        )
    }

    private fun calculateAlignment(
        value: Double,
        minVal: Double,
        maxVal: Double,
        rangeLow: Double,
        rangeHigh: Double
    ): Double {
        return when {
            value < minVal -> {
                val diff = minVal - value
                max(0.0, 1.0 - (diff / rangeLow))
            }
            value > maxVal -> {
                val diff = value - maxVal
                max(0.0, 1.0 - (diff / rangeHigh))
            }
            else -> 1.0
        }
    }
}
