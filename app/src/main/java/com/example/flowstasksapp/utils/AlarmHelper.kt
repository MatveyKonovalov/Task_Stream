package com.example.flowstasksapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.example.flowstasksapp.R
import com.example.flowstasksapp.domain.Task
import com.example.flowstasksapp.presentation.NotificationReceiver

object AlarmHelper {
    fun scheduleTask(context: Context, hour: Int, minute: Int, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent с данными уведомления
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", R.string.notification_about_task)
            putExtra("task", task.title)
            putExtra("id", task.id)
        }

        // Создание уникального requestCode
        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Расчёт времени
        val calendar = Calendar.getInstance().apply{
            // Когда была сделана таска
            set(Calendar.YEAR, task.date.year)
            set(Calendar.MONTH, task.date.month.value)
            set(Calendar.DAY_OF_MONTH, task.date.dayOfMonth)
            // Настройка пользователя
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }



    }
}