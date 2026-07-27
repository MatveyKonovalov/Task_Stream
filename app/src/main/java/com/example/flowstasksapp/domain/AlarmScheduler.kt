package com.example.flowstasksapp.domain

interface AlarmScheduler {
    fun scheduleTask(task: Task)
    fun cancelTask(task: Task)
}