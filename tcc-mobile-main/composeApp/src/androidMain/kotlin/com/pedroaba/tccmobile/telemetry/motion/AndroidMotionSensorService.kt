package com.pedroaba.tccmobile.telemetry.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.pedroaba.tccmobile.game.telemetry.model.AccelerationSample
import com.pedroaba.tccmobile.game.telemetry.model.MotionSensorType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

class AndroidMotionSensorService(
    context: Context
) : MotionSensorService {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override fun accelerationUpdates(config: MotionTrackingConfig): Flow<AccelerationSample> = callbackFlow {
        val selectedSensor = linearAccelerationSensor ?: accelerometerSensor
        if (selectedSensor == null) {
            close()
            return@callbackFlow
        }

        val gravity = FloatArray(3)
        val linearAcceleration = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val magnitude = when (event.sensor.type) {
                    Sensor.TYPE_LINEAR_ACCELERATION -> magnitudeOf(event.values)
                    else -> {
                        val alpha = 0.8f
                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                        linearAcceleration[0] = event.values[0] - gravity[0]
                        linearAcceleration[1] = event.values[1] - gravity[1]
                        linearAcceleration[2] = event.values[2] - gravity[2]
                        magnitudeOf(linearAcceleration)
                    }
                }

                trySend(
                    AccelerationSample(
                        timestampMs = sensorTimestampToEpochMs(event.timestamp),
                        magnitudeMetersPerSecondSquared = magnitude.toDouble(),
                        sensorType = if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                            MotionSensorType.LINEAR_ACCELERATION
                        } else {
                            MotionSensorType.ACCELEROMETER
                        }
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, selectedSensor, config.samplingPeriodUs)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    override fun isSensorAvailable(): Boolean {
        return linearAccelerationSensor != null || accelerometerSensor != null
    }

    private fun magnitudeOf(values: FloatArray): Float {
        return sqrt(
            values[0] * values[0] +
                values[1] * values[1] +
                values[2] * values[2]
        )
    }

    private fun sensorTimestampToEpochMs(eventTimestampNanos: Long): Long {
        val deltaMillis = (SystemClock.elapsedRealtimeNanos() - eventTimestampNanos) / 1_000_000L
        return System.currentTimeMillis() - deltaMillis
    }
}
