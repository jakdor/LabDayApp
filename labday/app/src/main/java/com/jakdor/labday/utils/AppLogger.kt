package com.jakdor.labday.utils

import android.content.Context
import com.jakdor.labday.BuildConfig
import timber.log.Timber

/**
 * Timber & Crashlytics libs configuration
 */
object AppLogger{

    @JvmStatic
    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
