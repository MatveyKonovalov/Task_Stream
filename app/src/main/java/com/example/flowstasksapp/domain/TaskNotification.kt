package com.example.flowstasksapp.domain

data class TaskNotification(
    val task: Task,
    val notification: Notification?
)