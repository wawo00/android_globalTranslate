package com.example.globaltranslate

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

object ActivityTracker: Application.ActivityLifecycleCallbacks {
    private var currentActivityRef: WeakReference<Activity>? = null

    /**
     * 在 Application 中注册此追踪器
     */
    public fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 获取当前前台 Activity
     * 注意：可能返回 null，因为使用了弱引用
     */
    val currentActivity: Activity?
        get() = currentActivityRef?.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        // Activity 进入前台
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // Activity 离开前台
        if (currentActivityRef?.get() == activity) {
            currentActivityRef = null
        }
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}