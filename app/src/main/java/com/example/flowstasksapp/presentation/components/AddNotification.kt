package com.example.flowstasksapp.presentation.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && !showBottomSheet) {
            changeBottomSheet()
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
                                if (checked){
                                    saveNotification(timePickerState.hour, timePickerState.minute)
                                }
                                scope.launch {
                                    sheetState.hide()
                                }
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
