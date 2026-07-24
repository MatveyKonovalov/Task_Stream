package com.example.flowstasksapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowstasksapp.domain.Repository
import com.example.flowstasksapp.domain.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // Дата
    private val _date = MutableStateFlow(LocalDate.now())
    val date = _date.asStateFlow()

    val tasks: StateFlow<List<Task>> = _date.flatMapLatest { date ->
        repository.getTasksByDate(date.getDateString())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Флаг открытого диалога добавления
    private val _isOpenAddScreen = MutableStateFlow(false)
    val isOpenAddScreen = _isOpenAddScreen.asStateFlow()

    //Флаг открытого диалога редактирования
    private val _isOpenUpdateTaskScreen = MutableStateFlow(false)
    val isOpenUpdateTaskScreen = _isOpenUpdateTaskScreen.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask = _selectedTask.asStateFlow()

    // Флаг загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Флаг ошибки
    private val _isErrorAdd = MutableStateFlow(false)
    val isErrorAdd = _isErrorAdd.asStateFlow()

    // Добавление задачи - БД сама уведомит об изменениях
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                withContext(Dispatchers.IO) {
                    repository.addTask(task)
                }
                closeAddScreen()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding task", e)
                closeAddScreen()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                withContext(Dispatchers.IO) {
                    repository.updateTask(task)
                }
                closeUpdateTaskScreen()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error updating task", e)
                closeUpdateTaskScreen()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                delay(1000L.milliseconds)

                withContext(Dispatchers.IO) {
                    repository.deleteTaskById(taskId)
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error deleting task", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    // Открытие и закрытие окна редактирования
    fun openAddScreen() {
        _isOpenAddScreen.value = true
    }

    fun closeAddScreen() {
        _isOpenAddScreen.value = false
    }

    // Открытие и закрытие окна редактирования
    fun openUpdateTaskScreen(task: Task) {
        _selectedTask.value = task
        _isOpenUpdateTaskScreen.value = true

    }

    fun closeUpdateTaskScreen() {
        _selectedTask.value = null
        _isOpenUpdateTaskScreen.value = false
    }

    fun showError() {
        viewModelScope.launch {
            _isErrorAdd.value = true
            delay(2000L.milliseconds)
            _isErrorAdd.value = false
        }
    }
}

fun LocalDate.getDateString(): String {
    return String.format("%02d.%02d.%04d", dayOfMonth, monthValue, year)
}