package com.pedroaba.tccmobile.game.telemetry.model

enum class TelemetryStrategy {
    MOVEMENT_ONLY,
    BPM_AND_MOVEMENT
}

enum class TelemetrySessionStatus {
    IDLE,
    RUNNING,
    PAUSED,
    STOPPED
}

enum class TelemetryIssue {
    LOCATION_PERMISSION_MISSING,
    LOCATION_PROVIDER_DISABLED,
    LOCATION_DATA_STALE,
    LOCATION_SENSOR_UNAVAILABLE,
    MOTION_SENSOR_UNAVAILABLE,
    WATCH_UNAVAILABLE
}
