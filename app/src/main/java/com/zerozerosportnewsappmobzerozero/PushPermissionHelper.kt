package com.zerozerosportnewsappmobzerozero

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal
import kotlinx.coroutines.launch

object PushPermissionHelper {

    private const val PREFS_NAME = "zerozero_push_prefs"
    private const val KEY_USER_DECLINED = "push_user_declined"

    fun promptIfNeeded(
        activity: AppCompatActivity,
        permissionLauncher: ActivityResultLauncher<String>,
    ) {
        val appId = activity.getString(R.string.onesignal_app_id)
        if (appId.isBlank() || appId == "YOUR_ONESIGNAL_APP_ID") {
            return
        }

        if (NotificationManagerCompat.from(activity).areNotificationsEnabled()) {
            registerWithOneSignal(activity)
            return
        }

        val prefs = activity.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_USER_DECLINED, false)) {
            return
        }

        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.push_permission_title)
            .setMessage(R.string.push_permission_message)
            .setCancelable(false)
            .setPositiveButton(R.string.push_permission_allow) { _, _ ->
                requestSystemPermission(activity, permissionLauncher)
            }
            .setNegativeButton(R.string.push_permission_later) { _, _ ->
                prefs.edit().putBoolean(KEY_USER_DECLINED, true).apply()
            }
            .show()
    }

    fun onSystemPermissionResult(activity: AppCompatActivity, granted: Boolean) {
        if (granted) {
            registerWithOneSignal(activity)
        }
    }

    private fun requestSystemPermission(
        activity: AppCompatActivity,
        permissionLauncher: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> registerWithOneSignal(activity)

                else -> permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            registerWithOneSignal(activity)
        }
    }

    private fun registerWithOneSignal(activity: AppCompatActivity) {
        activity.lifecycleScope.launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}
