package com.example.flowstasksapp.presentation.notificationutil

import android.content.Context
import com.example.flowstasksapp.domain.NotificationSender
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationSender(
        context: Context
    ): NotificationSender = NotificationSenderImpl(context)
}