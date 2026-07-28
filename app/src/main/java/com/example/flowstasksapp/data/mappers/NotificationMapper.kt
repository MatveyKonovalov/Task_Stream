package com.example.flowstasksapp.data.mappers

import com.example.flowstasksapp.data.database.entities.NotificationsEntity
import com.example.flowstasksapp.domain.Notification
import javax.inject.Inject

class NotificationMapper @Inject constructor() {
    fun toDomain(notificationsEntity: NotificationsEntity) = Notification(
        id = notificationsEntity.id,
        taskId = notificationsEntity.taskId,
        hour = notificationsEntity.hour,
        minute = notificationsEntity.minutes,
    )

    fun toEntity(notification: Notification) = NotificationsEntity(
        id = notification.id,
        taskId = notification.taskId,
        hour = notification.hour,
        minutes = notification.minute
    )
}