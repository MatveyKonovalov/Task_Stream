package com.example.flowstasksapp.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flowstasksapp.R
import com.example.flowstasksapp.domain.Task
import com.example.flowstasksapp.presentation.components.BottomSheetExample
import com.example.flowstasksapp.presentation.components.ShowDialog
import com.example.flowstasksapp.presentation.components.TaskCard
import com.example.flowstasksapp.presentation.components.WeeklyStrip
import java.time.LocalDate

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val tasks by viewModel.tasks.collectAsStateWithLifecycle() // все задачи на текущий день
    val curDate by viewModel.date.collectAsStateWithLifecycle() // текущая дата
    val isAdd by viewModel.isOpenAddScreen.collectAsStateWithLifecycle() // Открыто ли окно добавления
    val isError by viewModel.isErrorAdd.collectAsStateWithLifecycle() // ,Была ли ошибка
    val isUpdate by viewModel.isOpenUpdateTaskScreen.collectAsStateWithLifecycle() // Открыто ли окно редактирования
    val selectedTask by viewModel.selectedTask.collectAsStateWithLifecycle() // Выбранная задача
    val openNotificationScreen by viewModel.openNotificationScreen.collectAsStateWithLifecycle()
    // Окно добавления
    if (isAdd) {
        ShowDialog(
            viewModel::closeAddScreen,
            viewModel::addTask,
            curDate,
            isError,
            viewModel::showError,
            null,
            stringResource(R.string.add_task_title)
        )
    }

    // Окно редактирования
    if (isUpdate) {
        ShowDialog(
            viewModel::closeUpdateTaskScreen,
            viewModel::updateTask,
            curDate,
            isError,
            viewModel::showError,
            selectedTask,
            stringResource(R.string.update_title)
        )
    }

    // Панель добавления времемни реадикторвания
    BottomSheetExample(
        openNotificationScreen,
        viewModel::closeNotificationScreen,
        viewModel::addNotification
    )

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 40.dp, start = 5.dp, end = 5.dp)
    ) {
        Title(
            curDate = curDate,
            funcSetData = viewModel::setDate, viewModel::openAddScreen
        )
        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.no_task_provided),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .fillMaxWidth(0.9f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(16))
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .wrapContentSize(Alignment.Center)


            )
        } else {
            Tasks(
                tasks,
                viewModel::deleteTask,
                viewModel::openUpdateTaskScreen,
                viewModel::openNotificationScreen
            )
        }

    }

}

@Composable
private fun Title(
    curDate: LocalDate,
    funcSetData: (LocalDate) -> Unit,
    btnAdd: () -> Unit = {}
) {

    Column {
        // Вывод заголовка секции(Календарь)
        Text(
            text = stringResource(R.string.calendar),
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
        )
        // Недельная строка
        WeeklyStrip(curDate, funcSetData)

        // Список дел + добавить
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( // Список дел
                text = stringResource(R.string.todo_list),
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )


            Text(
                text = "+",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .width(60.dp)
                    .height(40.dp)
                    .padding(end = 10.dp, top = 10.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(30)
                    )
                    .clickable(onClick = btnAdd)

            )

        }

    }
}

@Composable
private fun Tasks(
    tasks: List<Task>,
    deleteTask: (Long) -> Unit,
    funcClick: (Task) -> Unit,
    funLongClick: (Task) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 15.dp, start = 5.dp, end = 5.dp, bottom = 40.dp)
    ) {


        items(items = tasks, key = { task -> task.id }) { task ->
            TaskCard(
                task, deleteTask,
                modifier = Modifier.combinedClickable(
                    onClick = { // При коротком нажатии
                        funcClick(task)
                        Log.w("Task edit", "${task.id}")
                    },
                    onDoubleClick = { deleteTask(task.id) }, // При двойном нажатии
                    onLongClick = { funLongClick(task) }
                )
            )
        }
    }
}

//@Composable
//private fun SwipeableTaskItem(
//    task: Task,
//    onDelete: (Long) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val dismissState = rememberSwipeToDismissBoxState(
//        confirmValueChange = { dismissValue ->
//            when (dismissValue) {
//                SwipeToDismissBoxValue.EndToStart -> { // Свайп влево
//                    onDelete(task.id)
//                    true
//                }
//
//                SwipeToDismissBoxValue.StartToEnd -> { // Свайп вправо
//                    // добавить переход к редактированию
//                    false
//                }
//
//                SwipeToDismissBoxValue.Settled -> false
//            }
//
//        }
//    )
//
//    SwipeToDismissBox(
//        state = dismissState,
//        backgroundContent = {
//            // ✅ Фон, который показывается при свайпе
//            when (dismissState.dismissDirection) {
//                SwipeToDismissBoxValue.EndToStart -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(MaterialTheme.colorScheme.onTertiaryFixed)
//                            .padding(end = 24.dp),
//                        contentAlignment = Alignment.CenterEnd
//                    ) {
//                        Text("Удалить", color = Color.White)
//                    }
//                }
//
//                SwipeToDismissBoxValue.StartToEnd -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(MaterialTheme.colorScheme.tertiary)
//                            .padding(start = 24.dp),
//                        contentAlignment = Alignment.CenterStart
//                    ) {
//                        Text("Редактировать", color = Color.White)
//                    }
//                }
//
//                else -> {}
//            }
//        },
//        modifier = modifier.fillMaxWidth()
//    ) {
//
//        TaskCard(
//            task = task,
//            deleteTaskById = onDelete,
//
//            )
//    }
//}

@Preview
@Composable
private fun ShowMainScreen() {
    MainScreen(hiltViewModel())
}