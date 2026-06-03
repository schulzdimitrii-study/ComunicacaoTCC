# Game Screens Organization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Separar GameScreen e seus componentes de App.kt para nova estrutura em features/game/screens/

**Architecture:** Criar diretório features/game/screens/ com GameScreen principal e subdiretório components/ para cards auxiliares. Atualizar App.kt para importar da nova localização.

**Tech Stack:** Kotlin, Jetpack Compose

---

### Task 1: Criar diretório components

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/`

- [ ] **Step 1: Verificar diretório pai**

Verificar se `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/` existe

```bash
ls -la composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/
```

Expected: Lista de subdiretórios (auth/, game/)

- [ ] **Step 2: Criar diretório screens/components**

```bash
mkdir -p composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components
```

---

### Task 2: Criar StatusRow.kt

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/StatusRow.kt`

- [ ] **Step 1: Criar arquivo StatusRow.kt**

```kotlin
package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/StatusRow.kt
git commit -m "feat: add StatusRow component"
```

---

### Task 3: Criar StatusDisplay.kt

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/StatusDisplay.kt`

- [ ] **Step 1: Criar arquivo StatusDisplay.kt**

```kotlin
package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.theme.AppTheme

@Composable
fun StatusDisplay(snapshot: GameSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusRow("Distance", "${snapshot.distance.toInt()}m")
            StatusRow("Score", "${(snapshot.performanceScore * 100).toInt()}%")
            StatusRow("Risk", "${(snapshot.risk * 100).toInt()}%")
            StatusRow("Horde Pressure", "${(snapshot.hordePressure * 100).toInt()}%")
            StatusRow("Result", snapshot.result.uppercase(), 
                color = when(snapshot.result) {
                    "escaped" -> AppTheme.colors.glow
                    "caught" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/StatusDisplay.kt
git commit -m "feat: add StatusDisplay component"
```

---

### Task 4: Criar SessionSignalCard.kt

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/SessionSignalCard.kt`

- [ ] **Step 1: Criar arquivo SessionSignalCard.kt**

```kotlin
package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.game.models.GameSnapshot
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.math.roundToInt

@Composable
fun SessionSignalCard(
    telemetryState: TelemetryState,
    snapshot: GameSnapshot
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StatusRow("Session", telemetryState.session.status.name)
            StatusRow("Telemetry Speed", telemetryState.latestSample?.speedMetersPerSecond?.let(::formatOneDecimal) ?: "--")
            StatusRow("Telemetry Distance", telemetryState.latestSample?.totalDistanceMeters?.roundToInt()?.toString() ?: "--")
            StatusRow("Runner Vel.", formatOneDecimal(snapshot.runnerVelocity))
            StatusRow("Horde Vel.", formatOneDecimal(snapshot.hordeVelocity))
            StatusRow("Elapsed", "${snapshot.elapsedSeconds.roundToInt()}s")
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    return ((value * 10.0).roundToInt() / 10.0).toString()
}
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/SessionSignalCard.kt
git commit -m "feat: add SessionSignalCard component"
```

---

### Task 5: Criar TelemetryStatusCard.kt

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/TelemetryStatusCard.kt`

- [ ] **Step 1: Criar arquivo TelemetryStatusCard.kt**

```kotlin
package com.pedroaba.tccmobile.features.game.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import kotlin.math.roundToInt

@Composable
fun TelemetryStatusCard(
    telemetryState: TelemetryState,
    lastEscapeMetricsLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "MOVEMENT METRICS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val sample = telemetryState.latestSample
            val metrics = telemetryState.latestEscapeMetrics

            StatusRow("Distance", sample?.totalDistanceMeters?.roundToInt()?.let { "${it}m" } ?: "--")
            StatusRow("Speed", sample?.speedMetersPerSecond?.let(::formatOneDecimal) ?: "--")
            StatusRow("Acceleration", sample?.effectiveAccelerationMetersPerSecondSquared?.let(::formatOneDecimal) ?: "--")
            StatusRow("Confidence", sample?.movementConfidence?.let { "${(it * 100).roundToInt()}%" } ?: "--")
            StatusRow("Escape Score", lastEscapeMetricsLabel)
            StatusRow("Normalized Speed", metrics?.normalizedSpeed?.let { "${(it * 100).roundToInt()}%" } ?: "--")
            StatusRow("Normalized Distance", metrics?.normalizedDistance?.let { "${(it * 100).roundToInt()}%" } ?: "--")

            val issuesLabel = telemetryState.availability.issues
                .filterNot { it.name == "WATCH_UNAVAILABLE" }
                .joinToString()
                .ifEmpty { "none" }

            Spacer(modifier = Modifier.height(8.dp))
            StatusRow("Issues", issuesLabel)
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    return ((value * 10.0).roundToInt() / 10.0).toString()
}
```

- [ ] **Step 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/TelemetryStatusCard.kt
git commit -m "feat: add TelemetryStatusCard component"
```

---

### Task 6: Criar GameScreen.kt

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/GameScreen.kt`

- [ ] **Step 1: Criar arquivo GameScreen.kt com imports e estrutura incompleta**

```kotlin
package com.pedroaba.tccmobile.features.game.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pedroaba.tccmobile.game.GameController
import com.pedroaba.tccmobile.game.KorgeGameView
import com.pedroaba.tccmobile.game.debug.GameDebugLogger
import com.pedroaba.tccmobile.game.models.SessionConfig
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySessionStatus
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryState
import com.pedroaba.tccmobile.features.game.screens.components.SessionSignalCard
import com.pedroaba.tccmobile.features.game.screens.components.StatusDisplay
import com.pedroaba.tccmobile.features.game.screens.components.TelemetryStatusCard
import com.pedroaba.tccmobile.theme.AppTheme
import com.pedroaba.tccmobile.theme.TccMobileTheme
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt
```

- [ ] **Step 2: Completar GameScreen.kt com o composable**

```kotlin
@Composable
fun GameScreen(
    telemetryStateFlow: StateFlow<TelemetryState>? = null,
    currentTimeMsProvider: () -> Long = { 0L },
    onStartTelemetrySession: (() -> Unit)? = null,
    onStopTelemetrySession: (() -> Unit)? = null
) {
    TccMobileTheme {
        val gameController = remember { GameController(timeProviderMs = currentTimeMsProvider) }
        val snapshot by gameController.snapshot.collectAsState()
        val isSceneLoading by gameController.isSceneLoading.collectAsState()
        val lastEscapeMetrics by gameController.lastEscapeMetrics.collectAsState()
        val isActive by gameController.isActive.collectAsState()
        val telemetryState = telemetryStateFlow?.collectAsState()?.value ?: TelemetryState()

        LaunchedEffect(telemetryState.latestEscapeMetrics) {
            telemetryState.latestEscapeMetrics?.let(gameController::applyEscapeMetrics)
        }

        LaunchedEffect(telemetryState.session.status) {
            when (telemetryState.session.status) {
                TelemetrySessionStatus.RUNNING -> {
                    if (!isActive) {
                        gameController.startSession(gameSessionConfig)
                    }
                }
                TelemetrySessionStatus.IDLE,
                TelemetrySessionStatus.STOPPED -> {
                    if (isActive) {
                        gameController.stopSession()
                    }
                }
                TelemetrySessionStatus.PAUSED -> Unit
            }
        }

        LaunchedEffect(snapshot.result, isActive, telemetryState.session.status) {
            val telemetryRunning = telemetryState.session.status == TelemetrySessionStatus.RUNNING ||
                telemetryState.session.status == TelemetrySessionStatus.PAUSED

            if (!isActive && telemetryRunning && (snapshot.result == "escaped" || snapshot.result == "caught")) {
                onStopTelemetrySession?.invoke()
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Game View Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black)
                ) {
                    KorgeGameView(
                        controller = gameController,
                        isActive = isActive,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isSceneLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AppTheme.colors.background.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Carregando sprites...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Debug & Status Panel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "SESSION STATUS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    StatusDisplay(snapshot = snapshot)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "GAME SESSION",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isSceneLoading) {
                                GameDebugLogger.log(
                                    "start-button",
                                )

                                return@Button
                            }

                            if (isActive) {
                                onStopTelemetrySession?.invoke()
                            } else {
                                onStartTelemetrySession?.invoke()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive)
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(if (isActive) "STOP SESSION" else "START SESSION")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SessionSignalCard(
                        telemetryState = telemetryState,
                        snapshot = snapshot
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TelemetryStatusCard(
                        telemetryState = telemetryState,
                        lastEscapeMetricsLabel = lastEscapeMetrics?.movementScore?.let { "${(it * 100).roundToInt()}%" } ?: "--"
                    )
                }
            }
        }
    }
}

private val gameSessionConfig = SessionConfig(
    goalDistance = 1000.0,
    sessionDurationSeconds = 90.0,
    initialDistance = 500.0,
    chaseRatePerSecond = 24.0,
    escapeRatePerSecond = 18.0
)
```

- [ ] **Step 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/GameScreen.kt
git commit -m "feat: add GameScreen"
```

---

### Task 7: Atualizar App.kt

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt`

- [ ] **Step 1: Ler App.kt atual**

```bash
cat composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt
```

Expected: Arquivo completo com imports, App(), GameScreen(), e componentes

- [ ] **Step 2: Reescrever App.kt apenas com App()**

```kotlin
package com.pedroaba.tccmobile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pedroaba.tccmobile.features.auth.screens.LoginScreen
import com.pedroaba.tccmobile.theme.TccMobileTheme

@Composable
@Preview
fun App() {
    TccMobileTheme {
        LoginScreen()
    }
}
```

- [ ] **Step 3: Compilar para verificar**

```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt
git commit -m "refactor: separate GameScreen from App.kt"
```

---

### Task 8: Revisão final

**Files:**
- Verificar: `composeApp/build.gradle.kts` (se necessário)

- [ ] **Step 1: Listar novos arquivos criados**

```bash
ls -la composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/
ls -la composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/game/screens/components/
```

Expected: Estrutura completa com todos os arquivos

- [ ] **Step 2: Compilar iOS**

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit final**

```bash
git add .
git commit -m "feat: reorganize game screens into features/game/screens/"
```