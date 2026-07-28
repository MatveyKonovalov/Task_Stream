package com.example.flowstasksapp.presentation.components

import android.Manifest
import android.R.attr.data
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.flowstasksapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetExample(
    showBottomSheet: Boolean,
    changeBottomSheet: () -> Unit,
    saveNotification: (Int, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val (checked, setChecked) = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 7,
        initialMinute = 0,
        is24Hour = true
    )

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }



    Column {
        if (showBottomSheet || sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = changeBottomSheet,
                sheetState = sheetState,
            ) {
                if (checked) PermissionScreen()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Title(checked = checked, onCheckedChange = setChecked)

                    // Время
                    if (checked) {
                        TimeInput(state = timePickerState)
                    }


                    // Кнопки
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (checked) {

                                    saveNotification(timePickerState.hour, timePickerState.minute)
                                }
                                scope.launch {
                                    sheetState.hide()
                                }
                                changeBottomSheet()
                            },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Text(stringResource(R.string.add_task), fontSize = 18.sp)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                }
                                changeBottomSheet()
                            },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Text(stringResource(R.string.cancel), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(10.dp)
    ) {
        Text(
            stringResource(R.string.notification_about_task),
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.onBackground,
                checkedThumbColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

@Composable
private fun PermissionScreen() {
    val context = LocalContext.current

    var notificationGranted by remember { mutableStateOf(false) }
    var exactAlarmGranted by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // Проверка Post_Notification
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { notificationGranted = it }

    // Проверка Schedule_Notification
    val alarmSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else true
    }

    // Начальная проверка
    LaunchedEffect(Unit) {
        notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else true

        loading = true

    }

    // Когда что-то не разрешено
    if (!notificationGranted && loading){
        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    if (!exactAlarmGranted && loading){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            alarmSettingsLauncher.launch(intent)
        }
    }


}

@Composable
private fun PermissionItem(
    title: String,
    granted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (granted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (granted) "✅ Разрешено" else "❌ Требуется разрешение",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!granted) {
                Button(onClick = onRequest) {
                    Text("Разрешить")
                }
            }
        }
    }
}