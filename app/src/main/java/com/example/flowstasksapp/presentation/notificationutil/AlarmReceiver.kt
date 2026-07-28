package com.example.flowstasksapp.presentation.notificationutil

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.flowstasksapp.domain.NotificationSender
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationSender: NotificationSender

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Напоминание о задаче"
        val task = intent.getStringExtra("task") ?: "У вас есть задача"
        val taskId = intent.getLongExtra("taskid", 0)

        notificationSender.sendNotification(title, task, taskId)
    }
}