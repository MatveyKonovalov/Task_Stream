package com.example.flowstasksapp.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskNotificationsEntity(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val notification: NotificationsEntity?
)