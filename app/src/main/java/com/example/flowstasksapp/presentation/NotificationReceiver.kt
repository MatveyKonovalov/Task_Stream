package com.example.flowstasksapp.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.snapshots.toInt
import androidx.core.app.NotificationCompat
import com.example.flowstasksapp.MainActivity
import com.example.flowstasksapp.R

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "habit_reminder_channel"
        const val CHANNEL_NAME = "Напоминания о задачах"
        const val NOTIFICATION_ID = 1001L
    }

    override fun onReceive(context: Context, intent: Intent) { // Получаем таску и отправляем её
        val title = intent.getStringExtra("title") ?: "Напоминание о задаче"
        val task = intent.getStringExtra("task")
        val id = intent.getLongExtra("taskid", NOTIFICATION_ID)

        task?.let { message ->
            sendNotification(context, title, message, id)
        }

    }

    private fun sendNotification(context: Context, title: String, task: String, id: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (channel == null){
            channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply{
                description = "Напоминание о задаче"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Интент для открытия приложения
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Создаём Intent
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

        notificationManager.notify(id.toInt(), notification)
    }
}