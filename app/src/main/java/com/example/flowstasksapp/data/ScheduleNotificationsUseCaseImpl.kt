package com.example.flowstasksapp.data

import com.example.flowstasksapp.domain.Repository
import com.example.flowstasksapp.domain.ScheduleNotificationsUseCase
import javax.inject.Inject

class ScheduleNotificationsUseCaseImpl @Inject constructor(
    private val repository: Repository
): ScheduleNotificationsUseCase {
    override suspend fun execute() {
        
    }
}