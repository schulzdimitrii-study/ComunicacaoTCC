package com.pedroaba.tccmobile.game.simulation

import com.pedroaba.tccmobile.game.models.BiofeedbackSample
import com.pedroaba.tccmobile.game.models.SessionConfig
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

object BiofeedbackSimulator {
    fun generateSample(
        mode: SimulationMode,
        elapsedSeconds: Int,
        manualBpm: Int,
        manualCadence: Int,
        config: SessionConfig,
        timestampMs: Long
    ): BiofeedbackSample {
        val sample = when (mode) {
            SimulationMode.STABLE -> stableSample(elapsedSeconds, config)
            SimulationMode.RECOVERING -> recoveringSample(elapsedSeconds, config)
            SimulationMode.STRUGGLING -> strugglingSample(elapsedSeconds, config)
            SimulationMode.INTERVAL -> intervalSample(elapsedSeconds, config)
            SimulationMode.CUSTOM -> BiofeedbackSample(
                bpm = manualBpm,
                cadence = manualCadence,
                timestampMs = timestampMs
            )
        }

        return sample.copy(timestampMs = timestampMs)
    }

    private fun stableSample(elapsedSeconds: Int, config: SessionConfig): BiofeedbackSample {
        val bpmCenter = (config.targetMinBpm + config.targetMaxBpm) / 2.0
        val cadenceCenter = (config.cadenceMin + config.cadenceMax) / 2.0
        return BiofeedbackSample(
            bpm = oscillate(bpmCenter, amplitude = 5.0, periodSeconds = 10.0, elapsedSeconds = elapsedSeconds),
            cadence = oscillate(cadenceCenter, amplitude = 4.0, periodSeconds = 8.0, elapsedSeconds = elapsedSeconds),
            timestampMs = 0L
        )
    }

    private fun recoveringSample(elapsedSeconds: Int, config: SessionConfig): BiofeedbackSample {
        val progress = (elapsedSeconds / 24.0).coerceIn(0.0, 1.0)
        val bpmStart = config.targetMaxBpm + 28.0
        val bpmEnd = (config.targetMinBpm + config.targetMaxBpm) / 2.0
        val cadenceStart = config.cadenceMin - 28.0
        val cadenceEnd = (config.cadenceMin + config.cadenceMax) / 2.0

        return BiofeedbackSample(
            bpm = lerp(bpmStart, bpmEnd, progress).roundToInt() + wave(amplitude = 3.0, periodSeconds = 7.0, elapsedSeconds = elapsedSeconds),
            cadence = lerp(cadenceStart, cadenceEnd, progress).roundToInt() + wave(amplitude = 2.0, periodSeconds = 6.0, elapsedSeconds = elapsedSeconds),
            timestampMs = 0L
        )
    }

    private fun strugglingSample(elapsedSeconds: Int, config: SessionConfig): BiofeedbackSample {
        val progress = (elapsedSeconds / 24.0).coerceIn(0.0, 1.0)
        val bpmStart = (config.targetMinBpm + config.targetMaxBpm) / 2.0
        val bpmEnd = config.targetMaxBpm + 35.0
        val cadenceStart = (config.cadenceMin + config.cadenceMax) / 2.0
        val cadenceEnd = config.cadenceMin - 35.0

        return BiofeedbackSample(
            bpm = lerp(bpmStart, bpmEnd, progress).roundToInt() + wave(amplitude = 4.0, periodSeconds = 8.0, elapsedSeconds = elapsedSeconds),
            cadence = lerp(cadenceStart, cadenceEnd, progress).roundToInt() + wave(amplitude = 3.0, periodSeconds = 5.0, elapsedSeconds = elapsedSeconds),
            timestampMs = 0L
        )
    }

    private fun intervalSample(elapsedSeconds: Int, config: SessionConfig): BiofeedbackSample {
        val cyclePosition = elapsedSeconds % 16
        return if (cyclePosition < 10) {
            stableSample(elapsedSeconds, config)
        } else {
            BiofeedbackSample(
                bpm = config.targetMaxBpm + 24 + wave(amplitude = 4.0, periodSeconds = 3.0, elapsedSeconds = elapsedSeconds),
                cadence = config.cadenceMin - 24 + wave(amplitude = 3.0, periodSeconds = 4.0, elapsedSeconds = elapsedSeconds),
                timestampMs = 0L
            )
        }
    }

    private fun oscillate(
        center: Double,
        amplitude: Double,
        periodSeconds: Double,
        elapsedSeconds: Int
    ): Int = (center + wave(amplitude, periodSeconds, elapsedSeconds)).roundToInt()

    private fun wave(amplitude: Double, periodSeconds: Double, elapsedSeconds: Int): Int {
        val radians = ((elapsedSeconds % periodSeconds) / periodSeconds) * PI * 2.0
        return (sin(radians) * amplitude).roundToInt()
    }

    private fun lerp(start: Double, end: Double, progress: Double): Double {
        return start + (end - start) * progress
    }
}
