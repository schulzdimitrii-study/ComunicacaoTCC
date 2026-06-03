# Spec: Godot to KorGE Game Migration

## Overview
This document describes the design for migrating an existing 2D mobile game (Runner vs Zombie Horde) from Godot to KorGE, integrated directly into a Compose Multiplatform application.

## 1. Architecture & Integration
The game will be implemented as a specialized component within the `composeApp` module, using KorGE as the rendering engine.

### Project Structure
The game files will be located in `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/`:
- `game/`: Entry point and orchestration.
    - `KorgeGameView.kt`: The Compose-KorGE bridge component.
    - `GameController.kt`: Bidirectional bridge for communication between Compose and KorGE.
- `game/scenes/`: Scene management.
    - `MainScene.kt`: The core gameplay scene.
- `game/entities/`: Visual and behavioral objects.
    - `Runner.kt`: Animated runner with procedural motion.
    - `ZombieHorde.kt`: Group of animated zombies.
    - `ZombieUnit.kt`: Individual zombie entity.
    - `ParallaxBackground.kt`: Multi-layered scrolling background.
- `game/logic/`: Pure Kotlin gameplay logic.
    - `RunCalculator.kt`: Mathematical engine for biofeedback processing.
- `game/models/`: Kotlin data classes.
    - `SessionConfig.kt`, `BiofeedbackSample.kt`, `GameSnapshot.kt`, `GameResult.kt`.

## 2. Data Flow & Communication
A `GameController` will serve as the single point of contact between the Compose UI and the KorGE engine.

### Inputs (Compose -> KorGE)
- `startSession(config: SessionConfig)`: Initializes the game state.
- `sendBiofeedback(sample: BiofeedbackSample)`: Updates the game with real-time data.
- `stopSession()`: Triggers the end of the session and result calculation.

### Outputs (KorGE -> Compose)
- `onStateUpdate(snapshot: GameSnapshot)`: Real-time telemetry for UI updates.
- `onSessionEnd(result: GameResult)`: Final session statistics.

### Gameplay Logic (`RunCalculator`)
Reimplementation of the Godot GDScript logic:
- BPM Alignment: `1.0 - (diff / range)`, clamped [0, 1]. Range: 35 for low, 25 for high.
- Cadence Alignment: Range: 30 for low, 20 for high.
- `performanceScore`: `bpmAlignment * 0.7 + cadenceAlignment * 0.3 - bpmStrain * 0.25`.
- `hordePressure`: `(1.0 - performanceScore) * 0.75 + bpmStrain * 0.25`.
- `risk`: `hordePressure * 0.8 + bpmStrain * 0.2`.

## 3. Visuals & Assets
### Asset Migration
- Path: `composeApp/src/commonMain/composeResources/files/game_assets/`.
- Backgrounds: `sky.png`, `skyline_back.png`, `skyline_mid.png`, `skyline_front.png`, `ground.png`, `fog.png`.
- Runner: `runner_blue.png`.
- Zombies: `Walk1.png` through `Walk6.png`.

### Entity Behavior
- **Runner**: Single sprite with procedural "breath" (idle) and "bounce" (run) animations using `onUpdate` or `tweens`.
- **Zombie Horde**: 10 units in formation. Each unit animates through the walk cycle. Formation density and "bobbing" depend on `threatPressure`.
- **Parallax**: 6 layers. Scroll speed = `100.0 + performanceScore * 150.0`.

## 4. Debug & Testing
- A debug overlay in the Compose UI will provide sliders/inputs for BPM and Cadence.
- The game will show a real-time HUD with Distance, Pressure, Risk, and Performance for validation.

## 5. Success Criteria
- No Godot runtime dependencies.
- Smooth 60 FPS performance on Android.
- Accurate reproduction of the Godot gameplay math.
- Animated runner and multiple-zombie horde.
- Working start/stop/update flow via `GameController`.
