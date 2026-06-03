package com.pedroaba.tccmobile.game.telemetry.usecase

import com.pedroaba.tccmobile.game.telemetry.model.BiofeedbackSample
import com.pedroaba.tccmobile.game.telemetry.model.TelemetryStrategy

class SelectTelemetryStrategyUseCase {
    operator fun invoke(
        biofeedbackSample: BiofeedbackSample?,
        isWatchConnected: Boolean
    ): TelemetryStrategy {
        return if (isWatchConnected && biofeedbackSample?.bpm != null) {
            TelemetryStrategy.BPM_AND_MOVEMENT
        } else {
            TelemetryStrategy.MOVEMENT_ONLY
        }
    }
}
