package com.example.flowstasksapp.data.mappers

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MappersModule {
    @Provides
    @Singleton
    fun providePriorityMapper(): PriorityMapper = PriorityMapper()

    @Provides
    @Singleton
    fun provideDateMapper(): DateMapper = DateMapper()

    @Provides
    @Singleton
    fun provideCategoryMapper(): CategoryMapper = CategoryMapper()

    @Provides
    @Singleton
    fun provideTaskMapper(
        categoryMapper: CategoryMapper,
        dateMapper: DateMapper,
        priorityMapper: PriorityMapper
    ): TaskMapper = TaskMapper(priorityMapper, categoryMapper, dateMapper)

    @Provides
    @Singleton
    fun provideTaskNotificationMapper(
        taskMapper: TaskMapper,
        notificationMapper: NotificationMapper
    ) = TaskNotificationMapper(taskMapper, notificationMapper)


}