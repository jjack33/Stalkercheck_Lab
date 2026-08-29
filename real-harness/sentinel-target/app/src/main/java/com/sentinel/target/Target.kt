package com.sentinel.target

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DeviceAdminReceiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Static screen describing the app, plus one button that starts a visible, no-op
 * foreground service. No monitoring, no data access.
 */
class TargetActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
            addView(TextView(this@TargetActivity).apply {
                textSize = 16f
                text = getString(R.string.target_explanation)
            })
            addView(Button(this@TargetActivity).apply {
                text = getString(R.string.start_benign_service)
                setOnClickListener {
                    val intent = Intent(this@TargetActivity, BenignForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                }
            })
        }
        setContentView(ScrollView(this).apply { addView(root) })
    }
}

/**
 * REAL accessibility service so the OS lists it and a tester can enable it, but it
 * performs NO monitoring: both callbacks are intentionally empty. It never reads
 * screen content, window state, or events.
 */
class BenignAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intentionally does nothing. This target observes no accessibility events.
    }

    override fun onInterrupt() {
        // Intentionally does nothing.
    }
}

/**
 * REAL notification-listener service so the OS lists it under Notification access,
 * but it never reads, stores, or forwards notification content: the callbacks are
 * empty.
 */
class BenignNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Intentionally does nothing. No notification content is ever accessed.
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Intentionally does nothing.
    }
}

/**
 * REAL VPN service declaration. It deliberately never calls VpnService.Builder or
 * establish(), so no tunnel exists and no traffic is captured, inspected, or routed.
 */
class BenignVpnService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // No tunnel is ever built. Stop immediately.
        stopSelf()
        return START_NOT_STICKY
    }
}

/**
 * REAL device-admin receiver so the OS lists it under Device admin apps, but it
 * enforces no policy and takes no action: every override is empty.
 */
class BenignDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        // Intentionally does nothing. No device policy is enforced.
    }

    override fun onDisabled(context: Context, intent: Intent) {
        // Intentionally does nothing.
    }
}

/**
 * Declares the RECEIVE_BOOT_COMPLETED persistence signal. It starts nothing and
 * performs no work; the body is intentionally empty.
 */
class BenignBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) = Unit
}

/**
 * A benign, clearly labeled foreground service a tester can start from the UI. It
 * posts a visible notification and does no work: no collection, no network, no
 * persistence beyond the visible notification.
 */
class BenignForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "sentinel_target_benign"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Sentinel Target (benign test)",
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }
        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Sentinel Target — benign test app")
            .setContentText("Visible no-op foreground service. Collects nothing.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }
}
