// data/scheduler/AlarmSchedulerImpl.kt
package com.example.flowstasksapp.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.flowstasksapp.R
import com.example.flowstasksapp.domain.AlarmScheduler
import com.example.flowstasksapp.domain.NotificationSender
import com.example.flowstasksapp.domain.Task
import com.example.flowstasksapp.presentation.AlarmReceiver
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    private val context: Context,
    private val notificationSender: NotificationSender  // Внедряем интерфейс
) : AlarmScheduler {

    companion object {
        private const val TAG = "AlarmScheduler"
    }

    override fun scheduleTask(task: Task) {
        // Проверка разрешений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Нет разрешения на точные будильники")
                return
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Расчет времени
        val calendar = Calendar.getInstance().apply {

            set(Calendar.YEAR, task.date.year)
            set(Calendar.MONTH, task.date.month.value)
            set(Calendar.DAY_OF_MONTH, task.date.dayOfMonth)

            set(Calendar.HOUR_OF_DAY, task.notificationTime.first)
            set(Calendar.MINUTE, task.notificationTime.second)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Если время прошло
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            return
        }

        // Создаем Intent для AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", context.getString(R.string.notification_about_task))
            putExtra("task", task.title)
            putExtra("taskid", task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d(TAG, "Будильник установлен для задачи ${task.id}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

    override fun cancelTask(task: Task) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)

            Log.d(TAG, "Будильник отменен для задачи ${task.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка отмены будильника: ${e.message}")
        }
    }
}