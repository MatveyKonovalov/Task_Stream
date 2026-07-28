package com.example.flowstasksapp.data.database

import android.content.Context
import com.example.flowstasksapp.data.database.daos.NotificationDao
import com.example.flowstasksapp.data.database.daos.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DateBaseModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): TaskDateBase =
        TaskDateBase.getInstance(context)

    @Provides
    @Singleton
    fun provideDataBaseDao(taskDateBase: TaskDateBase): TaskDao = taskDateBase.taskDao()

    @Provides
    @Singleton
    fun provideDataBaseNotificationDao(taskDateBase: TaskDateBase): NotificationDao =
        taskDateBase.notificationDao()
}