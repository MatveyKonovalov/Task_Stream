package com.example.flowstasksapp.domain

import java.time.LocalDate


data class Notification(
    val id: Long = 0,
    val taskId: Long,
    val hour: Int,
    val minute: Int,
)