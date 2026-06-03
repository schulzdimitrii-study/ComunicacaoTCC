package com.pedroaba.tccmobile.game.simulation

import com.pedroaba.tccmobile.game.logic.RunCalculator
import com.pedroaba.tccmobile.game.models.SessionConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BiofeedbackSimulatorTest {
    private val config = SessionConfig()
    private val calculator = RunCalculator()

    @Test
    fun customModeUsesManualValues() {
        val sample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.CUSTOM,
            elapsedSeconds = 12,
            manualBpm = 149,
            manualCadence = 177,
            config = config,
            timestampMs = 12000L
        )

        assertEquals(149, sample.bpm)
        assertEquals(177, sample.cadence)
        assertEquals(12000L, sample.timestampMs)
    }

    @Test
    fun stableModeStaysNearTargetZone() {
        val sample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.STABLE,
            elapsedSeconds = 8,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 8000L
        )

        assertTrue(sample.bpm in config.targetMinBpm..config.targetMaxBpm)
        assertTrue(sample.cadence in config.cadenceMin..config.cadenceMax)
    }

    @Test
    fun recoveringModeImprovesPerformanceOverTime() {
        val earlySample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.RECOVERING,
            elapsedSeconds = 0,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 0L
        )
        val lateSample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.RECOVERING,
            elapsedSeconds = 24,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 24000L
        )

        val earlySnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, earlySample, config)
        val lateSnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, lateSample, config)

        assertTrue(lateSnapshot.performanceScore > earlySnapshot.performanceScore)
    }

    @Test
    fun strugglingModeGetsWorseOverTime() {
        val earlySample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.STRUGGLING,
            elapsedSeconds = 0,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 0L
        )
        val lateSample = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.STRUGGLING,
            elapsedSeconds = 24,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 24000L
        )

        val earlySnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, earlySample, config)
        val lateSnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, lateSample, config)

        assertTrue(lateSnapshot.performanceScore < earlySnapshot.performanceScore)
        assertTrue(lateSnapshot.risk > earlySnapshot.risk)
    }

    @Test
    fun intervalModeAlternatesBetweenGoodAndBadPhases() {
        val goodPhase = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.INTERVAL,
            elapsedSeconds = 4,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 4000L
        )
        val badPhase = BiofeedbackSimulator.generateSample(
            mode = SimulationMode.INTERVAL,
            elapsedSeconds = 12,
            manualBpm = 0,
            manualCadence = 0,
            config = config,
            timestampMs = 12000L
        )

        val goodSnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, goodPhase, config)
        val badSnapshot = calculator.calculateSnapshot(500.0, 0.0, 1.0, badPhase, config)

        assertTrue(goodSnapshot.performanceScore > badSnapshot.performanceScore)
        assertTrue(goodSnapshot.risk < badSnapshot.risk)
    }
}
