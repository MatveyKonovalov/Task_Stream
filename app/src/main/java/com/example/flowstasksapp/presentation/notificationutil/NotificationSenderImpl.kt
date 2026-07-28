package com.example.flowstasksapp.presentation.notificationutil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.flowstasksapp.MainActivity
import com.example.flowstasksapp.R
import com.example.flowstasksapp.domain.NotificationSender
import javax.inject.Inject

class NotificationSenderImpl @Inject constructor(private val context: Context) :
    NotificationSender {
    companion object {
        const val CHANNEL_ID = "task_stream_receiver"
        const val CHANNEL_NAME = "Напоминание о задаче"
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
    ) {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC

            }
        notificationManager.createNotificationChannel(channel)
    }

    override fun sendNotification(title: String, task: String, taskId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал
        createNotificationChannel(notificationManager)

        // Intent для открытия приложения
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(), // id сообщения
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Создаём уведомление
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_adaptive)
            .setContentTitle(title)
            .setContentText(task)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }
}