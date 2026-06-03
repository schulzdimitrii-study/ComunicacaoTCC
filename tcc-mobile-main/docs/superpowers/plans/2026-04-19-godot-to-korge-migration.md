# Godot to KorGE Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate the 2D runner game from Godot to KorGE, integrating it as a Compose Multiplatform component.

**Architecture:** The game logic is decoupled into a pure Kotlin `RunCalculator`. A `GameController` manages the state and communication between Compose and KorGE. KorGE entities handle rendering and procedural animations.

**Tech Stack:** Kotlin, KorGE, Compose Multiplatform.

---

### Task 1: Setup Models and RunCalculator (TDD)

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/models/SessionConfig.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/models/BiofeedbackSample.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/models/GameSnapshot.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/models/GameResult.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/logic/RunCalculator.kt`
- Create: `composeApp/src/commonTest/kotlin/com/pedroaba/tccmobile/game/logic/RunCalculatorTest.kt`

- [ ] **Step 1: Write the failing test for RunCalculator**

```kotlin
package com.pedroaba.tccmobile.game.logic

import com.pedroaba.tccmobile.game.models.*
import kotlin.test.Test
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
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :composeApp:commonTest`
Expected: FAIL (Compilation error or missing classes)

- [ ] **Step 3: Implement Models and RunCalculator**

```kotlin
// SessionConfig.kt
data class SessionConfig(
    val goalDistance: Double = 1000.0,
    val sessionDurationSeconds: Double = 60.0,
    val chaseRatePerSecond: Double = 24.0,
    val escapeRatePerSecond = 18.0,
    val initialDistance: Double = 990.0,
    val targetMinBpm: Int = 108,
    val targetMaxBpm: Int = 135,
    val cadenceMin: Int = 160,
    val cadenceMax: Int = 180
)

// BiofeedbackSample.kt
data class BiofeedbackSample(val bpm: Int, val cadence: Int, val timestampMs: Long)

// GameSnapshot.kt
data class GameSnapshot(
    val distance: Double = 0.0,
    val hordePressure: Double = 0.0,
    val risk: Double = 0.0,
    val performanceScore: Double = 0.5,
    val runnerVelocity: Double = 0.0,
    val hordeVelocity: Double = 0.0,
    val elapsedSeconds: Double = 0.0,
    val result: String = "running"
)

// RunCalculator.kt
class RunCalculator {
    fun calculateSnapshot(
        currentDistance: Double,
        elapsedSeconds: Double,
        deltaSeconds: Double,
        sample: BiofeedbackSample,
        config: SessionConfig
    ): GameSnapshot {
        val bpmAlignment = calculateAlignment(sample.bpm, config.targetMinBpm, config.targetMaxBpm, 35.0, 25.0)
        val cadenceAlignment = calculateAlignment(sample.cadence, config.cadenceMin, config.cadenceMax, 30.0, 20.0)
        val bpmStrain = 1.0 - bpmAlignment

        val performanceScore = (bpmAlignment * 0.7 + cadenceAlignment * 0.3 - bpmStrain * 0.25).coerceIn(0.0, 1.0)
        val hordePressure = ((1.0 - performanceScore) * 0.75 + bpmStrain * 0.25).coerceIn(0.0, 1.0)
        val risk = (hordePressure * 0.8 + bpmStrain * 0.2).coerceIn(0.0, 1.0)

        val runnerVelocity = config.escapeRatePerSecond * performanceScore
        val hordeVelocity = config.chaseRatePerSecond * hordePressure
        val distance = currentDistance + (runnerVelocity - hordeVelocity) * deltaSeconds

        return GameSnapshot(
            distance = distance,
            hordePressure = hordePressure,
            risk = risk,
            performanceScore = performanceScore,
            runnerVelocity = runnerVelocity,
            hordeVelocity = hordeVelocity,
            elapsedSeconds = elapsedSeconds + deltaSeconds,
            result = if (distance <= 0) "caught" else if (distance >= config.goalDistance) "escaped" else "running"
        )
    }

    private fun calculateAlignment(value: Int, min: Int, max: Int, lowRange: Double, highRange: Double): Double {
        return when {
            value < min -> (1.0 - (min - value) / lowRange).coerceIn(0.0, 1.0)
            value > max -> (1.0 - (value - max) / highRange).coerceIn(0.0, 1.0)
            else -> 1.0
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :composeApp:commonTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/
git add composeApp/src/commonTest/kotlin/com/pedroaba/tccmobile/game/
git commit -m "feat: implement game models and run calculator with tests"
```

### Task 2: Implement GameController

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/GameController.kt`

- [ ] **Step 1: Implement GameController**

```kotlin
package com.pedroaba.tccmobile.game

import com.pedroaba.tccmobile.game.logic.RunCalculator
import com.pedroaba.tccmobile.game.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameController {
    private val calculator = RunCalculator()
    private var config = SessionConfig()
    
    private val _snapshot = MutableStateFlow(GameSnapshot())
    val snapshot: StateFlow<GameSnapshot> = _snapshot

    var isRunning: Boolean = false
        private set

    fun startSession(newConfig: SessionConfig) {
        config = newConfig
        _snapshot.value = GameSnapshot(distance = config.initialDistance)
        isRunning = true
    }

    fun stopSession() {
        isRunning = false
    }

    fun sendBiofeedback(sample: BiofeedbackSample) {
        if (!isRunning) return
        val current = _snapshot.value
        _snapshot.value = calculator.calculateSnapshot(
            currentDistance = current.distance,
            elapsedSeconds = current.elapsedSeconds,
            deltaSeconds = 1.0, // Assuming 1s updates for now
            sample = sample,
            config = config
        )
        if (_snapshot.value.result != "running") {
            stopSession()
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/GameController.kt
git commit -m "feat: add GameController for state management"
```

### Task 3: Asset Migration

- [ ] **Step 1: Copy assets from Godot project**

```bash
mkdir -p composeApp/src/commonMain/composeResources/files/game_assets/backgrounds
mkdir -p composeApp/src/commonMain/composeResources/files/game_assets/sprites/zombie
cp /Users/pedroaugustobarbosaaparecido/www/tcc-game/assets/backgrounds/*.png composeApp/src/commonMain/composeResources/files/game_assets/backgrounds/
cp /Users/pedroaugustobarbosaaparecido/www/tcc-game/assets/sprites/runner_blue.png composeApp/src/commonMain/composeResources/files/game_assets/sprites/
cp /Users/pedroaugustobarbosaaparecido/www/tcc-game/assets/sprites/zombie/Free-Zombie-Character-Sprite/Zombie1/animation/Walk*.png composeApp/src/commonMain/composeResources/files/game_assets/sprites/zombie/
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/composeResources/files/game_assets/
git commit -m "chore: migrate game assets from Godot project"
```

### Task 4: Implement KorGE Entities (Runner & Zombies)

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/entities/Runner.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/entities/ZombieHorde.kt`

- [ ] **Step 1: Implement Runner**

```kotlin
// Simplified Runner implementation
class Runner(val sprite: Sprite) : Container() {
    init {
        addChild(sprite)
    }
    
    fun updateAnimation(dt: TimeSpan, velocity: Double) {
        // Procedural bounce/breath logic here
    }
}
```

- [ ] **Step 2: Implement ZombieHorde**

```kotlin
// Simplified ZombieHorde implementation
class ZombieHorde(val zombieSprites: List<SpriteAnimation>) : Container() {
    // Manage 10 zombies in formation
}
```

- [ ] **Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/entities/
git commit -m "feat: implement Runner and ZombieHorde entities"
```

### Task 5: Implement MainScene and KorGE View

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/scenes/MainScene.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/KorgeGameView.kt`

- [ ] **Step 1: Implement MainScene**
- [ ] **Step 2: Implement KorgeGameView (Compose bridge)**
- [ ] **Step 3: Commit**

### Task 6: Integration and Debug UI

- [ ] **Step 1: Add sliders to Compose UI to send BiofeedbackSample to GameController**
- [ ] **Step 2: Final testing and Commit**
