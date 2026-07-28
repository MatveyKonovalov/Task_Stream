package com.example.flowstasksapp.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flowstasksapp.data.database.entities.NotificationTaskEntity
import com.example.flowstasksapp.data.database.entities.NotificationsEntity

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notificationsEntity: NotificationsEntity): Long

    @Delete
    suspend fun deleteNotification(notificationsEntity: NotificationsEntity): Int

    @Query("SELECT * FROM notifications LEFT JOIN tasks ON tasks.id = notifications.taskId")
    suspend fun getAllNotificationTasks(): List<NotificationTaskEntity>
}