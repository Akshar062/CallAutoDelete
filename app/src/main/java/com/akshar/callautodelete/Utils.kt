package com.akshar.callautodelete

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleFileCleanup(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<FileCleanupWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(calculateInitialDelayFor7AM(), TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "FileCleanup",
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

private fun calculateInitialDelayFor7AM(): Long {
    val currentTime = Calendar.getInstance()
    val sevenAM = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 7)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(currentTime)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return sevenAM.timeInMillis - currentTime.timeInMillis
}

fun createNotificationChannel(context: Context) {
    val name = "File Cleanup"
    val descriptionText = "Notifications for file cleanup service"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("delete_channel_id", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}