package com.example.flowstasksapp.data.mappers

import com.example.flowstasksapp.data.database.entities.NotificationTaskEntity
import com.example.flowstasksapp.data.database.entities.TaskNotificationsEntity
import com.example.flowstasksapp.domain.TaskNotification
import javax.inject.Inject

class TaskNotificationMapper @Inject constructor(
    private val taskMapper: TaskMapper,
    private val notificationMapper: NotificationMapper
) {
    fun toDomain(taskNotificationsEntity: TaskNotificationsEntity) = TaskNotification(
        task = taskMapper.toDomain(taskNotificationsEntity.task),
        notification = taskNotificationsEntity.notification?.let { notificationMapper.toDomain(it) }
    )

    fun toEntity(taskNotification: TaskNotification) = TaskNotificationsEntity(
        task = taskMapper.toEntity(taskNotification.task),
        notification = taskNotification.notification?.let { notificationMapper.toEntity(it) }
    )

    fun toDomain(notificationTaskEntity: NotificationTaskEntity) = TaskNotification(
        task = taskMapper.toDomain(notificationTaskEntity.taskEntity),
        notification = notificationMapper.toDomain(notificationTaskEntity.notification)
    )
}