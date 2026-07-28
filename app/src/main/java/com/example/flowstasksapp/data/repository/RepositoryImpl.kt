package com.example.flowstasksapp.data.repository

import com.example.flowstasksapp.data.database.daos.NotificationDao
import com.example.flowstasksapp.data.database.daos.TaskDao
import com.example.flowstasksapp.data.database.entities.NotificationsEntity
import com.example.flowstasksapp.data.mappers.TaskMapper
import com.example.flowstasksapp.data.mappers.TaskNotificationMapper
import com.example.flowstasksapp.domain.Repository
import com.example.flowstasksapp.domain.Task
import com.example.flowstasksapp.domain.TaskNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val taskMapper: TaskMapper,
    private val taskDao: TaskDao,
    private val notificationDao: NotificationDao,
    private val taskNotificationMapper: TaskNotificationMapper

) : Repository {
    override fun getTasksByDate(date: String): Flow<List<Task>> {
        return taskDao.getTasksByDate(date).map { entities ->
            taskMapper.toDomain(entities)
        }
    }

    override suspend fun addTask(task: Task): Long = taskDao.addTask(taskMapper.toEntity(task))
    override suspend fun updateTask(task: Task) = taskDao.updateTask(taskMapper.toEntity(task))
    override suspend fun deleteTaskById(taskId: Long): Int = taskDao.deleteTask(taskId)

    override suspend fun addNotification(taskId: Long, hour: Int, minute: Int): Long =
        notificationDao.insertNotification(
            NotificationsEntity(
                taskId = taskId,
                hour = hour,
                minutes = minute
            )
        )

    override suspend fun getTaskWithNotification(): List<TaskNotification> {
        return notificationDao.getAllNotificationTasks().map { taskNotificationMapper.toDomain(it) }
    }
}