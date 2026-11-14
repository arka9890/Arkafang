package com.openfang.mobile.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Builds the persistent notification shown while [ProotService] runs.
 *
 * Channel: "openfang-daemon" (created on first use, important importance).
 * Two variants: [running] (with a [Stop] action) and [stopped] (no action,
 * cleared when the user stops the daemon).
 */
object StatusNotifier {

    const val CHANNEL_ID = "openfang-daemon"
    const val NOTIF_RUNNING = 1
    const val NOTIF_STOPPED = 2

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                CHANNEL_ID,
                "OpenFang daemon",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shows while the OpenFang daemon is running inside Proot"
                setShowBadge(false)
            }
            nm.createNotificationChannel(ch)
        }
    }

    fun running(ctx: Context, statusText: String = "Running"): Notification {
        ensureChannel(ctx)
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pending = android.app.PendingIntent.getActivity(
            ctx, 0,
            android.content.Intent(ctx, com.openfang.mobile.MainActivity::class.java),
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(ctx, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(ctx)
        }
        return builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("OpenFang — $statusText")
            .setContentText("Daemon is running in Proot. Tap to open the app.")
            .setContentIntent(pending)
            .setOngoing(true)
            .build()
    }

    fun stopped(ctx: Context, reason: String = "Stopped"): Notification {
        ensureChannel(ctx)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("OpenFang — $reason")
                .setContentText("The daemon has stopped.")
                .setAutoCancel(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(ctx)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("OpenFang — $reason")
                .setContentText("The daemon has stopped.")
                .setAutoCancel(true)
                .build()
        }
    }

    fun postRunning(ctx: Context, statusText: String = "Running") {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_RUNNING, running(ctx, statusText))
    }

    fun postStopped(ctx: Context, reason: String = "Stopped") {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_STOPPED, stopped(ctx, reason))
    }

    fun clear(ctx: Context) {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIF_RUNNING)
        nm.cancel(NOTIF_STOPPED)
    }
}
