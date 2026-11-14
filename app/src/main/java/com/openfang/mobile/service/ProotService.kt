package com.openfang.mobile.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Foreground service that holds the Proot + OpenFang daemon alive
 * while the user hasn't explicitly stopped it.
 *
 * - [startForeground] shows a persistent notification (Review Focus #4
 *   — user can tap the notification to get back to the app, and stop
 *   via the in-app button).
 * - A watchdog coroutine polls [ProotOrchestrator.status] every 30 s.
 *   If the daemon dies unexpectedly, the service posts a "restart?"
 *   notification variant.
 *
 * Declared in AndroidManifest as `foregroundServiceType="specialUse"`.
 * No BOOT_COMPLETED receiver — start is manual (spec §3, §6.6).
 */
@AndroidEntryPoint
class ProotService : Service() {

    @Inject lateinit var orchestrator: ProotOrchestrator

    private val watchdog = Executors.newSingleThreadScheduledExecutor()

    companion object {
        const val TAG = "ProotService"
        const val ACTION_STOP = "com.openfang.mobile.action.STOP"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // Bind a shared orchestrator instance (injected via Hilt)
        orchestrator.currentPaths() // force init
        StatusNotifier.postRunning(this)
        startForeground(
            StatusNotifier.NOTIF_RUNNING,
            StatusNotifier.running(this),
        )
        scheduleWatchdog()
        Log.i(TAG, "service created, watchdog scheduled")
    }

    private fun scheduleWatchdog() {
        watchdog.scheduleAtFixedRate({
            val st = orchestrator.status()
            Log.d(TAG, "watchdog: $st")
            if (st is ProotOrchestrator.DaemonStatus.Stopped) {
                // Daemon died. Post a different notification so the
                // user knows to restart. Don't stop the service itself
                // (the service still wants to show the status).
                runOnUiThread {
                    StatusNotifier.postRunning(this@ProotService, "Stopped unexpectedly — tap to restart")
                }
            }
        }, 0, 30, TimeUnit.SECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            orchestrator.stop()
            stopSelf()
            return START_NOT_STICKY
        }
        // Started with no action → ensure the daemon is running
        orchestrator.start()
        return START_STICKY
    }

    private fun runOnUiThread(block: () -> Unit) {
        mainLooperHandler.post { block() }
    }

    private val mainLooperHandler = android.os.Handler(mainLooper)

    override fun onDestroy() {
        super.onDestroy()
        watchdog.shutdownNow()
        orchestrator.stop()
        StatusNotifier.clear(this)
        Log.i(TAG, "service destroyed")
    }
}
