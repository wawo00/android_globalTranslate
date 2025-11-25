package com.example.globaltranslate

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ActivityTracker.register(this)
    }
}