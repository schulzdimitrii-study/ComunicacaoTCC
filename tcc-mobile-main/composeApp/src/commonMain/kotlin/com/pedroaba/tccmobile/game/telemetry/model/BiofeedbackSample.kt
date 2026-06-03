package com.pedroaba.tccmobile.game.telemetry.model

data class BiofeedbackSample(
    val timestampMs: Long,
    val bpm: Int? = null,
    val source: BiofeedbackSource = BiofeedbackSource.NONE
)

enum class BiofeedbackSource {
    NONE,
    WATCH,
    PHONE
}
