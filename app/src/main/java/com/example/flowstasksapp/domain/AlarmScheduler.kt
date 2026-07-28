package com.example.flowstasksapp.domain

interface AlarmScheduler {
    fun scheduleTask(task: Task, hour: Int, minute: Int)
    fun cancelTask(task: Task)
}