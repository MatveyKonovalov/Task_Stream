package com.example.flowstasksapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationsEntity(
    @PrimaryKey
    val id: Long = 0,
    val taskId: Long,
    val hour: Int,
    val minutes: Int
)