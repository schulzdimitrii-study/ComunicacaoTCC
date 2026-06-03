package com.pedroaba.tccmobile.game.entities

import com.pedroaba.tccmobile.game.GameAssets
import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*

class ZombieUnit : Container() {
    private lateinit var sprite: Sprite
    private var baseFormationOffset = Point(0.0, 0.0)

    // Constants from Godot reference
    private val BASE_SCALE = 0.15
    private val BASE_VISUAL_OFFSET = Point(-14.0, -57.0)
    private val BASE_FPS = 10.0

    suspend fun load() {
        val frames = (1..6).map {
            resourcesVfs["${GameAssets.root}/sprites/zombie/Walk$it.png"].readBitmap().slice()
        }
        val animation = SpriteAnimation(
            sprites = frames,
            defaultTimePerFrame = (1000.0 / BASE_FPS).milliseconds
        )
        sprite = sprite(animation) {
            anchor(0.5, 1.0)
            scale(BASE_SCALE)
            position(BASE_VISUAL_OFFSET)
            playAnimationLooped()
        }
    }

    fun configureUnit(
        formationOffset: Point,
        scaleMultiplier: Double,
        speedMultiplier: Double,
        alpha: Double
    ) {
        this.baseFormationOffset = formationOffset
        position(formationOffset)
        sprite.scale(BASE_SCALE * scaleMultiplier)
        sprite.alpha = alpha
        sprite.speed = speedMultiplier
    }

    fun setBobOffset(yOffset: Double) {
        position(baseFormationOffset.x, baseFormationOffset.y + yOffset)
    }
}
