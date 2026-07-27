package com.example.flowstasksapp.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.flowstasksapp.data.scheduler.AlarmSchedulerImpl
import com.example.flowstasksapp.domain.AlarmScheduler
import com.example.flowstasksapp.domain.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

// Создание ресивера (что сохранить расписание уведомлений после перезагрузки)
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var scheduler: AlarmScheduler
    override fun onReceive(
        context: Context,
        intent: Intent
    ) { // Вызывается когда происходит событие
        // Если устройство перезагрузилось
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.getTaskWithNotification().forEach { task ->
                    try {
                        scheduler.scheduleTask(task)
                    } catch (_: SecurityException) {
                    }

                }
            }
        }
    }
}
