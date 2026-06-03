package com.pedroaba.tccmobile.game.entities

import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.*

class ZombieHorde : Container() {
    private val hordeUnits = mutableListOf<ZombieUnit>()
    private var threatPressure = 1.0

    // Constants from Godot reference
    private val INITIAL_X = 20.0
    private val GROUND_Y = ParallaxBackground.STREET_TOP_Y + 4.0
    private val BASE_APPROACH_RANGE = 100.0
    private val PRESSURE_DENSE_FACTOR = 0.52
    private val PRESSURE_LOOSE_FACTOR = 1.18
    private val PRESSURE_ALPHA_FLOOR = 0.45
    private val PRESSURE_BOB_DISTANCE = 4.0

    private data class Anchor(
        val name: String,
        val offset: Point,
        val scale: Double,
        val speed: Double,
        val alpha: Double,
        val z: Int
    )

    private val FORMATION_ANCHORS = listOf(
        Anchor("ZombieFront", Point(0.0, 0.0), 1.08, 1.05, 1.0, 4),
        Anchor("ZombieMid1", Point(-22.0, -2.0), 0.98, 0.98, 0.9, 3),
        Anchor("ZombieMid2", Point(20.0, -1.0), 0.95, 1.02, 0.9, 3),
        Anchor("ZombieMid3", Point(-4.0, -3.0), 0.92, 1.01, 0.88, 3),
        Anchor("ZombieMid4", Point(44.0, -2.0), 0.9, 0.99, 0.84, 3),
        Anchor("ZombieBack1", Point(-42.0, -4.0), 0.84, 0.94, 0.72, 2),
        Anchor("ZombieBack2", Point(38.0, -3.0), 0.82, 0.96, 0.72, 2),
        Anchor("ZombieBack3", Point(-62.0, -6.0), 0.76, 0.92, 0.62, 1),
        Anchor("ZombieBack4", Point(60.0, -5.0), 0.74, 0.95, 0.62, 1),
        Anchor("ZombieBack5", Point(16.0, -7.0), 0.7, 0.9, 0.58, 1)
    )

    suspend fun load() {
        // We sort by z index to maintain visual depth
        val sortedAnchors = FORMATION_ANCHORS.sortedBy { it.z }
        
        for (anchor in sortedAnchors) {
            val unit = ZombieUnit()
            unit.load()
            hordeUnits.add(unit)
            addChild(unit)
        }
        
        position(INITIAL_X, GROUND_Y)
        applyHordeFormation()
    }

    fun setThreatPressure(pressure: Double) {
        threatPressure = pressure.coerceIn(0.0, 1.0)
        
        // Update horde position relative to initial
        val xPos = INITIAL_X + (1.0 - threatPressure) * BASE_APPROACH_RANGE
        position(xPos, GROUND_Y)
        
        applyHordeFormation()
    }

    private fun applyHordeFormation() {
        if (hordeUnits.isEmpty()) return

        val spacingFactor = lerp(PRESSURE_LOOSE_FACTOR, PRESSURE_DENSE_FACTOR, threatPressure)
        val bobLift = threatPressure * PRESSURE_BOB_DISTANCE

        for (i in hordeUnits.indices) {
            val unit = hordeUnits[i]
            val anchor = FORMATION_ANCHORS[i] // Using original order for anchor matching
            
            val formationOffset = Point(
                anchor.offset.x * spacingFactor,
                anchor.offset.y - bobLift
            )
            
            val speedMultiplier = lerp(anchor.speed * 0.92, anchor.speed * 1.08, threatPressure)
            val alpha = max(PRESSURE_ALPHA_FLOOR, lerp(anchor.alpha * 0.8, anchor.alpha, threatPressure))
            
            unit.configureUnit(formationOffset, anchor.scale, speedMultiplier, alpha)
        }
    }

    fun animateHordeMotion(time: TimeSpan) {
        val swayStrength = lerp(0.8, 1.8, threatPressure)
        val seconds = time.seconds

        for (i in hordeUnits.indices) {
            val unit = hordeUnits[i]
            val phase = seconds * (3.2 + i * 0.15)
            unit.setBobOffset(sin(phase) * swayStrength)
        }
    }

    private fun lerp(start: Double, end: Double, fraction: Double): Double {
        return start + fraction * (end - start)
    }
}
