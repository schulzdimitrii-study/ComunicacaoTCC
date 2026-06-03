package com.pedroaba.tccmobile.telemetry.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pedroaba.tccmobile.MainActivity
import com.pedroaba.tccmobile.R
import com.pedroaba.tccmobile.game.telemetry.usecase.TelemetryNotificationContent

class TelemetryNotificationFactory(
    private val context: Context
) {
    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Telemetry tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows the active movement session status."
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    fun create(content: TelemetryNotificationContent): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            1001,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(content.title)
            .setContentText(content.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.body))
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "telemetry_tracking"
        const val NOTIFICATION_ID = 1001
    }
}
