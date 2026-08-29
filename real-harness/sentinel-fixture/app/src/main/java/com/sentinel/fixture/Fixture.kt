package com.sentinel.fixture

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView

/** Static screen stating what this app is. No behavior beyond displaying text. */
class FixtureActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            setPadding(48, 96, 48, 48)
            textSize = 16f
            text = getString(R.string.fixture_explanation)
        })
    }
}

/**
 * Inert declaration-only components. They are never exported, carry no intent
 * filters, are never bound or invoked, and contain no functionality. They exist
 * solely so Sentinel's scanner can observe their manifest declarations.
 */
class InertDeclarationServiceOne : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}

class InertDeclarationServiceTwo : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}

class InertDeclarationServiceThree : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}

class InertDeclarationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) = Unit
}
