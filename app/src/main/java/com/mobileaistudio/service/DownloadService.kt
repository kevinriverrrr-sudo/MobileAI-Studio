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
        val modelName = intent?.getStringExtra(EXTRA_MODEL_NAME) ?: "Модель"
        val downloadUrl = intent?.getStringExtra(EXTRA_DOWNLOAD_URL) ?: ""
        val fileName = intent?.getStringExtra(EXTRA_FILE_NAME) ?: "model.gguf"

        if (downloadUrl.isEmpty()) {
            // No URL provided — stop immediately
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = createProgressNotification(modelName, 0)
        startForeground(NOTIFICATION_ID, notification)

        // Cancel any existing download
        downloadJob?.cancel()
        downloadJob = serviceScope.launch {
            downloadFile(downloadUrl, fileName, modelName)
        }

        return START_REDELIVER_INTENT
    }

    private suspend fun downloadFile(urlString: String, fileName: String, modelName: String) {
        var connection: HttpURLConnection? = null
        val tmpFile = File(getExternalFilesDir(null), "models/${fileName}.tmp")
        val finalFile = File(getExternalFilesDir(null), "models/$fileName")

        // If final file already exists, skip download
        if (finalFile.exists()) {
            showCompletionNotification(modelName)
            Log.i(TAG, "File already exists: $fileName")
            stopSelf()
            return
        }

        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 300000  // 5 min
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Download failed: ${connection.responseCode}")
                showErrorNotification(modelName, "HTTP ${connection.responseCode}")
                stopSelf()
                return
            }

            val fileSize = connection.contentLength
            val modelsDir = File(getExternalFilesDir(null), "models")
            if (!modelsDir.exists()) modelsDir.mkdirs()

            // Clean up any previous partial download
            if (tmpFile.exists()) tmpFile.delete()

            connection.inputStream.use { input ->
                FileOutputStream(tmpFile).use { output ->
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

            // Atomic rename: tmp -> final
            tmpFile.renameTo(finalFile)

            showCompletionNotification(modelName)
            Log.i(TAG, "Download complete: $fileName")
            stopSelf()
        } catch (e: CancellationException) {
            Log.i(TAG, "Download cancelled")
            // Clean up partial file
            if (tmpFile.exists()) tmpFile.delete()
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Download error", e)
            // Clean up partial file
            if (tmpFile.exists()) tmpFile.delete()
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
        const val EXTRA_MODEL_NAME = "model_name"
        const val EXTRA_DOWNLOAD_URL = "download_url"
        const val EXTRA_FILE_NAME = "file_name"

        fun startDownload(context: Context, modelName: String, downloadUrl: String, fileName: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_MODEL_NAME, modelName)
                putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
