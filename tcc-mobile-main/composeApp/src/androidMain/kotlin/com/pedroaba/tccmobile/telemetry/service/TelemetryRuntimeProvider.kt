package com.pedroaba.tccmobile.telemetry.service

import android.content.Context

object TelemetryRuntimeProvider {
    @Volatile
    private var runtime: AndroidTelemetryRuntime? = null

    fun get(context: Context): AndroidTelemetryRuntime {
        return runtime ?: synchronized(this) {
            runtime ?: AndroidTelemetryRuntime.create(context.applicationContext).also {
                runtime = it
            }
        }
    }
}
