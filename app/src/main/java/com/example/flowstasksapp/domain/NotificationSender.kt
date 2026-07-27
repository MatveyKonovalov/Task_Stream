package com.example.flowstasksapp.domain

interface NotificationSender {
    fun sendNotification(title: String, task: String, taskId: Long)
}