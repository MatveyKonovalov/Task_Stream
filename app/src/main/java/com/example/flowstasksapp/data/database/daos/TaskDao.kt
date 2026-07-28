package com.example.flowstasksapp.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flowstasksapp.data.database.entities.TaskEntity
import com.example.flowstasksapp.data.database.entities.TaskNotificationsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskEntity: TaskEntity): Long // Возвращённый id

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long): Int

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Query("""
    SELECT * FROM tasks 
    LEFT JOIN notifications ON tasks.id = notifications.taskId
    WHERE tasks.id = :taskId 
""")
    suspend fun getTaskWithNotification(taskId: Long): TaskNotificationsEntity?
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity
    // Suspend методы для реального кода
    @Insert
    fun addTaskTest(task: TaskEntity): Long

}