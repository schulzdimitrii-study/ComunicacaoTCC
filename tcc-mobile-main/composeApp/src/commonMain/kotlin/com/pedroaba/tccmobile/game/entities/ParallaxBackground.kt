package com.pedroaba.tccmobile.game.entities

import com.pedroaba.tccmobile.game.GameAssets
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*

class ParallaxBackground : Container() {
    companion object {
        const val STREET_TOP_Y = 286.0
    }

    private val layers = mutableListOf<ParallaxLayer>()
    private var baseSpeed = 0.0

    private data class ParallaxLayer(
        val view: Container,
        val multiplier: Double,
        val width: Double
    )

    private val LAYER_CONFIGS = listOf(
        LayerConfig("sky", 0.0, 0.0),
        LayerConfig("skyline_back", 0.1, 92.0),
        LayerConfig("skyline_mid", 0.25, 104.0),
        LayerConfig("skyline_front", 0.5, 118.0),
        LayerConfig("ground", 1.0, STREET_TOP_Y),
        LayerConfig("fog", 1.2, STREET_TOP_Y - 20.0)
    )

    private data class LayerConfig(
        val name: String,
        val multiplier: Double,
        val yPos: Double
    )

    suspend fun load() {
        for (config in LAYER_CONFIGS) {
            val bitmap = resourcesVfs["${GameAssets.root}/backgrounds/${config.name}.png"].readBitmap()
            val layerContainer = container()
            
            // We create two instances of the image for seamless scrolling
            val img1 = layerContainer.image(bitmap) {
                position(0.0, config.yPos)
            }
            val img2 = layerContainer.image(bitmap) {
                position(bitmap.width.toDouble(), config.yPos)
            }
            
            layers.add(ParallaxLayer(layerContainer, config.multiplier, bitmap.width.toDouble()))
        }
    }

    fun updateSpeed(performanceScore: Double) {
        baseSpeed = 100.0 + performanceScore * 150.0
    }

    fun update(dt: TimeSpan) {
        val delta = dt.seconds
        for (layer in layers) {
            if (layer.multiplier == 0.0) continue
            
            val movement = baseSpeed * layer.multiplier * delta
            layer.view.x -= movement
            
            // Wrap around
            if (layer.view.x <= -layer.width) {
                layer.view.x += layer.width
            }
        }
    }
}
