package com.mobileaistudio.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mobileaistudio.MobileAIApplication
import com.mobileaistudio.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class DownloadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var downloadJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val modelName = intent?.getStringExtra("model_name") ?: "Модель"
        val downloadUrl = intent?.getStringExtra("download_url") ?: ""
        val fileName = intent?.getStringExtra("file_name") ?: "model.gguf"
        val notification = createProgressNotification(modelName, 0)
        startForeground(NOTIFICATION_ID, notification)

        if (downloadUrl.isNotEmpty()) {
            downloadJob = serviceScope.launch {
                downloadFile(downloadUrl, fileName, modelName)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun downloadFile(urlString: String, fileName: String, modelName: String) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 120000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Download failed: ${connection.responseCode}")
                stopSelf()
                return
            }

            val fileSize = connection.contentLength
            val modelsDir = File(getExternalFilesDir(null), "models")
            if (!modelsDir.exists()) modelsDir.mkdirs()
            val outputFile = File(modelsDir, fileName)

            connection.inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8192)
                    var totalRead = 0L
                    var lastUpdate = 0L

                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        totalRead += read

                        val now = System.currentTimeMillis()
                        if (now - lastUpdate > 500) {
                            lastUpdate = now
                            val progress = if (fileSize > 0) (totalRead * 100 / fileSize).toInt() else 0
                            updateNotification(modelName, progress)
                        }
                    }
                }
            }

            // Download complete
            showCompletionNotification(modelName)
            Log.i(TAG, "Download complete: $fileName")
            stopSelf()
        } catch (e: CancellationException) {
            Log.i(TAG, "Download cancelled")
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Download error", e)
            showErrorNotification(modelName, e.message ?: "Неизвестная ошибка")
            stopSelf()
        } finally {
            connection?.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createProgressNotification(modelName: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, MobileAIApplication.CHANNEL_DOWNLOAD)
            .setContentTitle("Скачивание модели")
            .setContentText(modelName)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(modelName: String, progress: Int) {
        val notification = createProgressNotification(modelName, progress)
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun showCompletionNotification(modelName: String) {
        val notification = NotificationCompat.Builder(this, MobileAIApplication.CHANNEL_COMPLETION)
            .setContentTitle("Скачивание завершено")
            .setContentText("$modelName успешно скачана")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun showErrorNotification(modelName: String, error: String) {
        val notification = NotificationCompat.Builder(this, MobileAIApplication.CHANNEL_COMPLETION)
            .setContentTitle("Ошибка скачивания")
            .setContentText("$modelName: $error")
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setAutoCancel(true)
            .build()
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID + 2, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val TAG = "DownloadService"

        fun startDownload(context: Context, modelName: String, downloadUrl: String, fileName: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra("model_name", modelName)
                putExtra("download_url", downloadUrl)
                putExtra("file_name", fileName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
