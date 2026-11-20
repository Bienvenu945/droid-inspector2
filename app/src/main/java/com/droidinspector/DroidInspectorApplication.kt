package com.droidinspector

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * DROID INSPECTOR X
 * Application Entry Point
 * Signature: @Bienvenu
 */
@HiltAndroidApp
class DroidInspectorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization for diagnostics engine and caching
    }
}
