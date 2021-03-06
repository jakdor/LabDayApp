package com.jakdor.labday.utils

import android.content.Context
import androidx.annotation.Nullable
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jakdor.labday.BuildConfig
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Timber & Crashlytics libs configuration
 */
object AppLogger{

    @JvmStatic
    fun init(context: Context) {
        val core = CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build()
        Fabric.with(context, Crashlytics.Builder().core(core).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    /**
     * Log tree for production crash reporting via Crashlytics
     */
    private class CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, @Nullable tag: String?,
                         @Nullable message: String, @Nullable t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }

            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

            if (t == null) {
                Crashlytics.logException(Exception(message))
            } else {
                Crashlytics.logException(t)
            }
        }

        companion object {
            const val CRASHLYTICS_KEY_PRIORITY = "priority"
            const val CRASHLYTICS_KEY_TAG = "tag"
            const val CRASHLYTICS_KEY_MESSAGE = "message"
        }
    }
}
