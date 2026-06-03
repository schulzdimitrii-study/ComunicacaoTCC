package com.pedroaba.tccmobile.game.logic

import com.pedroaba.tccmobile.game.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RunCalculatorTest {
    @Test
    fun testCalculateSnapshot() {
        val calculator = RunCalculator()
        val config = SessionConfig(
            targetMinBpm = 100,
            targetMaxBpm = 140,
            initialDistance = 100.0,
            goalDistance = 1000.0,
            escapeRatePerSecond = 20.0,
            chaseRatePerSecond = 15.0
        )
        val sample = BiofeedbackSample(bpm = 120, cadence = 170, timestampMs = 1000)
        
        val snapshot = calculator.calculateSnapshot(
            currentDistance = 100.0,
            elapsedSeconds = 0.0,
            deltaSeconds = 1.0,
            sample = sample,
            config = config
        )
        
        assertTrue(snapshot.performanceScore > 0.0, "Performance score should be positive")
        assertTrue(snapshot.distance > 100.0, "Distance should increase if performance is good")
        assertEquals(1.0, snapshot.elapsedSeconds, 0.001)
    }

    @Test
    fun testBpmAlignmentBelowRange() {
        val calculator = RunCalculator()
        val config = SessionConfig(targetMinBpm = 100, targetMaxBpm = 140)
        // BPM 90: diff = 100 - 90 = 10. Range low = 35. Alignment = 1.0 - (10/35) = 0.714
        val sample = BiofeedbackSample(bpm = 90, cadence = 170, timestampMs = 0)
        val snapshot = calculator.calculateSnapshot(100.0, 0.0, 1.0, sample, config)
        
        // bpmAlignment = 0.7142857
        // cadenceAlignment = 1.0
        // bpmStrain = 1.0 - 0.7142857 = 0.2857143
        // performanceScore = 0.7142857 * 0.7 + 1.0 * 0.3 - 0.2857143 * 0.25 = 0.5 + 0.3 - 0.0714 = 0.7286
        assertEquals(0.7286, snapshot.performanceScore, 0.001)
    }

    @Test
    fun testGameStatusEscaped() {
        val calculator = RunCalculator()
        val config = SessionConfig(goalDistance = 1000.0)
        val sample = BiofeedbackSample(bpm = 120, cadence = 170, timestampMs = 0)
        val snapshot = calculator.calculateSnapshot(1000.0, 0.0, 1.0, sample, config)
        assertEquals("escaped", snapshot.result)
    }

    @Test
    fun testGameStatusCaught() {
        val calculator = RunCalculator()
        val config = SessionConfig(goalDistance = 1000.0)
        val sample = BiofeedbackSample(bpm = 80, cadence = 100, timestampMs = 0)
        // Set distance to a small value and low performance to ensure caught
        val snapshot = calculator.calculateSnapshot(-1.0, 0.0, 1.0, sample, config)
        assertEquals("caught", snapshot.result)
    }
}
