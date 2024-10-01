package com.akshar.callautodelete

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.documentfile.provider.DocumentFile
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class FileCleanupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val folderUri = sharedPreferences.getString("FOLDER_URI", null)

        folderUri?.let { s ->
            val documentFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(s))
            if (documentFile != null && documentFile.isDirectory) {
                val files = documentFile.listFiles()
                val sortedFiles = files.sortedByDescending { it.lastModified() }

                // Calculate the cutoff time for 7 days
                val cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)

                // Delete old files
                val oldFiles = sortedFiles.filter { it.lastModified() < cutoffTime }
                oldFiles.forEach { it.delete() }

                // Show notification on completion
                showNotification("File Cleanup", "Deleted ${oldFiles.size} old recordings.")
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "delete_channel_id")
            .setSmallIcon(R.drawable.baseline_auto_delete_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
    }
}