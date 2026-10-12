package com.zerozerosportnewsappmobzerozero

import android.app.Application
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
class ZeroZeroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val appId = getString(R.string.onesignal_app_id)
        if (appId.isBlank() || appId == "YOUR_ONESIGNAL_APP_ID") {
            return
        }

        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, appId)
    }
}
