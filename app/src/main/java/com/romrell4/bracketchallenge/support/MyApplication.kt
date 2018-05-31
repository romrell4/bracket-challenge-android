package com.romrell4.bracketchallenge.support

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle

class MyApplication: Application() {
	override fun onCreate() {
		super.onCreate()

		registerActivityLifecycleCallbacks(object: ActivityLifecycleCallbacks {
			override fun onActivityPaused(activity: Activity?) {}

			override fun onActivityResumed(activity: Activity?) {}

			override fun onActivityStarted(activity: Activity?) {}

			override fun onActivityDestroyed(activity: Activity?) {}

			override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

			override fun onActivityStopped(activity: Activity?) {}

			override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
				activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
			}

		})
	}
}