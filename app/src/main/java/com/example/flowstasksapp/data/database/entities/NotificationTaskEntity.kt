package com.example.flowstasksapp.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


data class NotificationTaskEntity(
    @Embedded
    val notification: NotificationsEntity,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "id"
    )
    val taskEntity: TaskEntity

)