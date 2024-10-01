package com.akshar.callautodelete

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
        scheduleFileCleanup(this)
    }
}