package com.pedroaba.tccmobile.game.scenes

import com.pedroaba.tccmobile.game.GameController
import com.pedroaba.tccmobile.game.entities.ParallaxBackground
import com.pedroaba.tccmobile.game.entities.Runner
import com.pedroaba.tccmobile.game.entities.ZombieHorde
import com.pedroaba.tccmobile.game.visual.DistanceVisualMapper
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import korlibs.time.TimeSpan

class MainScene(private val controller: GameController, private val isActive: Boolean) : Scene() {
    companion object {
        private const val RUNNER_MIN_X = 128.0
        private const val RUNNER_MAX_X = 380.0
    }

    private lateinit var background: ParallaxBackground
    private lateinit var horde: ZombieHorde
    private lateinit var runner: Runner
    private var totalTime = TimeSpan.ZERO

    override suspend fun SContainer.sceneMain() {
        controller.onSceneLoadingStarted()

        // Load and add Parallax Background
        background = ParallaxBackground().also { it.load() }
        addChild(background)

        // Load and add Zombie Horde
        horde = ZombieHorde().also { it.load() }
        addChild(horde)

        // Load and add Runner
        runner = Runner().also { it.load() }
        runner.position(
            DistanceVisualMapper.runnerX(
                distance = controller.sessionConfig.value.initialDistance,
                goalDistance = controller.sessionConfig.value.goalDistance,
                minX = RUNNER_MIN_X,
                maxX = RUNNER_MAX_X
            ),
            ParallaxBackground.STREET_TOP_Y + 4.0
        )
        addChild(runner)

        controller.onSceneLoaded()

        // Main game loop
        addUpdater { dt ->
            totalTime += dt
            val snapshot = controller.snapshot.value
            
            // Update Parallax Background
            background.updateSpeed(snapshot.performanceScore)
            background.update(dt)

            // Update Horde position and internal animation
            horde.setThreatPressure(snapshot.hordePressure)
            horde.animateHordeMotion(totalTime)

            runner.setTrackX(
                DistanceVisualMapper.runnerX(
                    distance = snapshot.distance,
                    goalDistance = controller.sessionConfig.value.goalDistance,
                    minX = RUNNER_MIN_X,
                    maxX = RUNNER_MAX_X
                )
            )

            // Update Runner animation
            runner.setRunning(isActive)
            runner.updateAnimation(dt)
        }
    }
}
