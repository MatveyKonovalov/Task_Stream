package com.example.flowstasksapp.presentation.components

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
    var isAllPermission by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val toastMessage = stringResource(R.string.no_permission)
    val (isNotificationEnabled, setNotificationEnabled) = remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Title(
                        checked = isNotificationEnabled,
                        onCheckedChange = setNotificationEnabled
                    )

                    if (isNotificationEnabled) {
                        // Показываем экран разрешений только если они еще не получены
                        if (!isAllPermission) {
                            PermissionScreen(
                                onAllPermissionsGranted = {
                                    isAllPermission = true
                                }
                            )
                        }

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
                                if (isNotificationEnabled) {
                                    if (isAllPermission) {
                                        saveNotification(
                                            timePickerState.hour,
                                            timePickerState.minute
                                        )
                                    } else {
                                        showCustomToast(
                                            context = context,
                                            message = toastMessage,
                                            backgroundColor = backgroundColor,
                                            textColor = textColor
                                        )
                                    }
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
private fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    var notificationGranted by remember { mutableStateOf(false) }
    var exactAlarmGranted by remember { mutableStateOf(false) }
    var hasCheckedPermissions by remember { mutableStateOf(false) }
    val loading = rememberSaveable { mutableStateOf(true) }

    // Launcher для POST_NOTIFICATIONS (только для Android 13+)
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationGranted = isGranted
    }

    // Launcher для точных будильников (только для Android 12+)
    val alarmSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true
        }
    }

    // Первоначальная проверка разрешений
    LaunchedEffect(Unit) {
        notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Для версий до Android 13 разрешение не требуется
        }

        exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true // Для версий до Android 12 разрешение не требуется
        }

        hasCheckedPermissions = true
        loading.value = false
    }

    // Запрос недостающих разрешений
    LaunchedEffect(hasCheckedPermissions) {
        if (hasCheckedPermissions) {
            // Запрашиваем уведомления только на Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!notificationGranted) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // Запрашиваем точные будильники только на Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!exactAlarmGranted) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    alarmSettingsLauncher.launch(intent)
                }
            }
        }
    }

    // Отслеживаем изменения разрешений
    LaunchedEffect(notificationGranted, exactAlarmGranted) {
        if (hasCheckedPermissions && notificationGranted && exactAlarmGranted) {
            onAllPermissionsGranted()
        }
    }

    // UI для отображения статуса разрешений
    if (!loading.value) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.permissions_required),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Показываем пункт с уведомлениями только на Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionItem(
                    title = stringResource(R.string.notifications_permission),
                    granted = notificationGranted,
                    onRequest = {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }

            // Показываем пункт с будильниками только на Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PermissionItem(
                    title = stringResource(R.string.exact_alarm_permission),
                    granted = exactAlarmGranted,
                    onRequest = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        alarmSettingsLauncher.launch(intent)
                    }
                )
            }
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
            containerColor = if (granted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (granted) {
                        "✅ ${stringResource(R.string.permission_granted)}"
                    } else {
                        "❌ ${stringResource(R.string.permission_required)}"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!granted) {
                Button(onClick = onRequest) {
                    Text(stringResource(R.string.allow))
                }
            }
        }
    }
}

