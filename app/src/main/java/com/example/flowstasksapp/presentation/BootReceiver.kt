package com.example.flowstasksapp.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.flowstasksapp.domain.Repository
import javax.inject.Inject

// Создание ресивера (что сохранить расписание уведомлений после перезагрузки)
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: Repository

    override fun onReceive(
        context: Context,
        intent: Intent
    ) { // Вызывается когда происходит событие
        // Если устройство перезагрузилось
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO
        }
    }
}
