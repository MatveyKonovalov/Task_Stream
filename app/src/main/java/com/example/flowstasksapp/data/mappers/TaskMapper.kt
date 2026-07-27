package com.example.flowstasksapp.data.mappers

import com.example.flowstasksapp.data.database.entities.TaskEntity
import com.example.flowstasksapp.domain.Task
import javax.inject.Inject

class TaskMapper @Inject constructor(
    private val priorityMapper: PriorityMapper,
    private val categoryMapper: CategoryMapper,
    private val dateMapper: DateMapper,
) {
    fun toDomain(taskEntity: TaskEntity): Task = Task(
        id = taskEntity.id,
        title = taskEntity.title,
        description = taskEntity.description,
        priority = priorityMapper.toDomain(taskEntity.priority),
        category = categoryMapper.toDomain(taskEntity.category),
        isComplete = false,
        date = dateMapper.toDomain(taskEntity.date)
    )

    fun toDomain(tasksEntity: List<TaskEntity>): List<Task> {
        return tasksEntity.map { entity -> toDomain(entity) }
    }

    fun toEntity(task: Task): TaskEntity = TaskEntity(
        id = task.id,
        title = task.title,
        description = task.description,
        priority = priorityMapper.toEntity(task.priority),
        category = categoryMapper.toEntity(task.category),
        date = task.getDate()
    )
}