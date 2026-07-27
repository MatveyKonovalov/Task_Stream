package com.example.flowstasksapp.domain

import androidx.compose.runtime.Immutable
import java.time.LocalDate


sealed class Priority {
    object Easy : Priority()
    object Medium : Priority()
    object Hard : Priority()
}

enum class Category(val title: String) {
    Work("Работа"),
    SelfDevelopment("Личное саморазвитие"),
    HealthAndFitness("Здоровье и спорт"),
    HomeAndChores("Дом и быт"),
    Finances("Финансы"),
    FamilyAndFriends("Семья и друзья"),
    CreativityAndHobbies("Творчество и хобби"),
    Errands("Административные дела"),
    Leisure("Отдых и развлечения"),
    Projects("Долгосрочные цели"),
    Unknown("Неизвестная")
}

@Immutable
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: Priority = Priority.Easy,
    val category: Category = Category.Unknown,
    val isComplete: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val notificationTime: Pair<Int, Int> = Pair(0, 0) // часы, минуты
) {
    @JvmName("getDateString")
    fun getDate(): String {
        val year: Int = date.year
        val month: Int = date.monthValue
        val day: Int = date.dayOfMonth

        return String.format("%02d.%02d.%04d", day, month, year)
    }
}