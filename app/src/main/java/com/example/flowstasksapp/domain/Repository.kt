package com.example.flowstasksapp.domain

import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getTasksByDate(date: String): Flow<List<Task>>
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTaskById(taskId: Long): Int
}