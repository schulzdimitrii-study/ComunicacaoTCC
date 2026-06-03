package com.pedroaba.tccmobile.telemetry.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.Service
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pedroaba.tccmobile.game.telemetry.model.TelemetrySessionStatus
import com.pedroaba.tccmobile.game.telemetry.usecase.TelemetryNotificationFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TelemetryForegroundService : Service() {
    private val notificationFormatter = TelemetryNotificationFormatter()
    private lateinit var notificationFactory: TelemetryNotificationFactory
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var telemetryJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        notificationFactory = TelemetryNotificationFactory(this)
        notificationFactory.ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val runtime = TelemetryRuntimeProvider.get(this)
        val initialContent = notificationFormatter.format(runtime.repository.telemetryState.value)
        startForeground(
            TelemetryNotificationFactory.NOTIFICATION_ID,
            notificationFactory.create(initialContent)
        )

        telemetryJob?.cancel()
        telemetryJob = serviceScope.launch {
            runtime.repository.telemetryState.collectLatest { state ->
                when (state.session.status) {
                    TelemetrySessionStatus.IDLE,
                    TelemetrySessionStatus.STOPPED -> stopTelemetryForeground()
                    else -> NotificationManagerCompat.from(this@TelemetryForegroundService).notify(
                        TelemetryNotificationFactory.NOTIFICATION_ID,
                        notificationFactory.create(notificationFormatter.format(state))
                    )
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        telemetryJob?.cancel()
        serviceScope.coroutineContext.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopTelemetryForeground() {
        telemetryJob?.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TelemetryForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TelemetryForegroundService::class.java))
        }
    }
}
