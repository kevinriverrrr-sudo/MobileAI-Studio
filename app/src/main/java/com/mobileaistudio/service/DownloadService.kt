package com.mobileaistudio.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mobileaistudio.MobileAIApplication
import com.mobileaistudio.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val modelName = intent?.getStringExtra("model_name") ?: "Модель"
        val notification = createProgressNotification(modelName, 0)
        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createProgressNotification(modelName: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, MobileAIApplication.CHANNEL_DOWNLOAD)
            .setContentTitle("Скачивание модели")
            .setContentText(modelName)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }
}
