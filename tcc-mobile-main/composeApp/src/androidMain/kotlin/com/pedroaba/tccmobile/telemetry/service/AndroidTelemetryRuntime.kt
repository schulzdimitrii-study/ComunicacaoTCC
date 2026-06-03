package com.pedroaba.tccmobile.telemetry.service

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.pedroaba.tccmobile.telemetry.data.DefaultTelemetryRepository
import com.pedroaba.tccmobile.telemetry.data.TelemetryRepository
import com.pedroaba.tccmobile.telemetry.location.FusedLocationTrackingService
import com.pedroaba.tccmobile.telemetry.motion.AndroidMotionSensorService
import com.pedroaba.tccmobile.telemetry.wear.AndroidWearTelemetryBridge

class AndroidTelemetryRuntime private constructor(
    val repository: TelemetryRepository,
    val permissionChecker: TelemetryPermissionChecker
) {
    companion object {
        fun create(context: Context): AndroidTelemetryRuntime {
            val appContext = context.applicationContext
            val permissionChecker = TelemetryPermissionChecker(appContext)
            val repository = DefaultTelemetryRepository(
                locationTrackingService = FusedLocationTrackingService(
                    context = appContext,
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
                ),
                motionSensorService = AndroidMotionSensorService(appContext),
                wearTelemetryBridge = AndroidWearTelemetryBridge(appContext)
            )

            return AndroidTelemetryRuntime(
                repository = repository,
                permissionChecker = permissionChecker
            )
        }
    }
}
