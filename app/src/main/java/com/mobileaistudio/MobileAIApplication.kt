package com.mobileaistudio

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MobileAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_DOWNLOAD,
                "Скачивание моделей",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Прогресс скачивания моделей" },
            NotificationChannel(
                CHANNEL_COMPLETION,
                "Завершение операций",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Уведомления о завершении скачивания" },
            NotificationChannel(
                CHANNEL_INFERENCE,
                "Инференс",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Статус работы AI модели" }
        )
        val nm = getSystemService(NotificationManager::class.java)
        channels.forEach { nm.createNotificationChannel(it) }
    }

    companion object {
        const val CHANNEL_DOWNLOAD = "download_channel"
        const val CHANNEL_COMPLETION = "completion_channel"
        const val CHANNEL_INFERENCE = "inference_channel"
    }
}
