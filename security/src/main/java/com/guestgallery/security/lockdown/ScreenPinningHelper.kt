package com.guestgallery.security.lockdown

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for Android's screen-pinning (lock task) feature.
 *
 * Screen pinning locks the user into a single app until they authenticate
 * to leave. This class wraps the platform API and adds pre-condition checks.
 *
 * **Note:** Full lock-task mode requires the app to be a device-owner or
 * profile-owner. The standard [Activity.startLockTask] shows a system
 * confirmation dialog for non-owner apps.
 */
@Singleton
class ScreenPinningHelper
    @Inject
    constructor() {
        /**
         * Returns `true` if the device supports screen pinning.
         * Screen pinning is available on Android 5.0 (API 21) and above;
         * since this app's `minSdk` is 26, it is always supported.
         */
        val isSupported: Boolean = true // minSdk 26 >= API 21

        /**
         * Returns `true` if the activity is currently in lock-task (screen-pinning) mode.
         */
        fun isScreenPinningActive(activity: Activity): Boolean {
            val activityManager =
                activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
            } else {
                @Suppress("DEPRECATION")
                activityManager.isInLockTaskMode
            }
        }

        /**
         * Requests screen pinning for the given activity.
         *
         * On non-device-owner apps the system will show a confirmation dialog.
         */
        fun requestScreenPinning(activity: Activity) {
            activity.startLockTask()
        }

        /**
         * Stops screen pinning for the given activity.
         *
         * Safe to call even if the activity is not currently pinned.
         */
        fun stopScreenPinning(activity: Activity) {
            try {
                activity.stopLockTask()
            } catch (_: IllegalStateException) {
                // Activity was not in lock-task mode — nothing to do.
            }
        }
    }
