package com.pedroaba.tccmobile.game.entities

import com.pedroaba.tccmobile.game.GameAssets
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.*

class Runner : Container() {
    private lateinit var spriteImage: Image
    private var animationTime = 0.0
    private var isRunning = false

    // Constants from Godot reference
    private val RUNNER_SCALE = 0.06
    private val RUNNER_VISUAL_OFFSET = Point(-22.0, -56.0)
    private val IDLE_BREATH_SPEED = 2.0
    private val IDLE_BREATH_BOB = 2.5
    private val RUN_BOUNCE_SPEED = 10.0
    private val RUN_BOUNCE_HEIGHT = 5.0

    suspend fun load() {
        val bitmap = resourcesVfs["${GameAssets.root}/sprites/runner_blue.png"].readBitmap()
        spriteImage = image(bitmap) {
            anchor(0.5, 0.5) // Adjusting anchor to center for better scaling/rotation
            scale(RUNNER_SCALE)
            position(RUNNER_VISUAL_OFFSET)
        }
    }

    fun setRunning(running: Boolean) {
        isRunning = running
    }

    fun setTrackX(x: Double) {
        this.x = x
    }

    fun updateAnimation(dt: TimeSpan) {
        animationTime += dt.seconds
        
        if (!isRunning) {
            // IDLE: Breath animation
            val breath = sin(animationTime * IDLE_BREATH_SPEED)
            spriteImage.position(
                RUNNER_VISUAL_OFFSET.x,
                RUNNER_VISUAL_OFFSET.y + breath * IDLE_BREATH_BOB
            )
            spriteImage.scale(
                RUNNER_SCALE * (1.0 + breath * 0.02),
                RUNNER_SCALE * (1.0 + breath * 0.02)
            )
            spriteImage.rotation = (breath * 0.02).radians
        } else {
            // RUN: Bounce animation
            val runWave = sin(animationTime * RUN_BOUNCE_SPEED)
            val stride = sin(animationTime * RUN_BOUNCE_SPEED * 0.5)
            
            spriteImage.position(
                RUNNER_VISUAL_OFFSET.x + stride * 1.5,
                RUNNER_VISUAL_OFFSET.y + abs(runWave) * -RUN_BOUNCE_HEIGHT
            )
            spriteImage.scale(
                RUNNER_SCALE * (1.0 + abs(runWave) * 0.08),
                RUNNER_SCALE * (1.0 - abs(runWave) * 0.06)
            )
            spriteImage.rotation = (stride * 0.05).radians
        }
    }
}
