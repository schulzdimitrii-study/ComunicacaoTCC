package com.pedroaba.tccmobile.telemetry.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.pedroaba.tccmobile.game.telemetry.model.LocationPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FusedLocationTrackingService(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationTrackingService {
    @SuppressLint("MissingPermission")
    override fun locationUpdates(config: LocationTrackingConfig): Flow<LocationPoint> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            config.updateIntervalMs
        )
            .setMinUpdateIntervalMillis(config.minUpdateIntervalMs)
            .setMinUpdateDistanceMeters(config.minUpdateDistanceMeters)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySend(
                        LocationPoint(
                            timestampMs = location.time,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracyMeters = location.accuracy.toDouble(),
                            altitudeMeters = location.altitude,
                            bearingDegrees = location.bearing.toDouble(),
                            speedMetersPerSecond = location.speed.toDouble(),
                            speedAccuracyMetersPerSecond = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                location.speedAccuracyMetersPerSecond.toDouble()
                            } else {
                                null
                            },
                            provider = location.provider ?: "fused"
                        )
                    )
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
    }

    override fun isLocationEnabled(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
