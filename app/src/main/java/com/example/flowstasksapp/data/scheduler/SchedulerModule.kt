package com.example.flowstasksapp.data.scheduler

import android.content.Context
import com.example.flowstasksapp.domain.AlarmScheduler
import com.example.flowstasksapp.domain.NotificationSender
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        context: Context,
        notificationSender: NotificationSender
    ): AlarmScheduler {
        return AlarmSchedulerImpl(context, notificationSender)
    }
}