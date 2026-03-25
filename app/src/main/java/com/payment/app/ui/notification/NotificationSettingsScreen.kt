package com.payment.app.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.db.entity.NotificationSettingEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var enabled by remember { mutableStateOf(false) }
    var leadDays by remember { mutableStateOf("3") }
    var budgetThreshold by remember { mutableStateOf("80") }
    var monthlyDay by remember { mutableStateOf("1") }
    var themeMode by remember { mutableStateOf("system") }
    var themeAccent by remember { mutableStateOf("ocean") }
    var lockEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.settings, uiState.themeMode, uiState.themeAccent, uiState.lockEnabled) {
        val settings = uiState.settings
        enabled = settings?.enabled ?: false
        leadDays = (settings?.reminderLeadDays ?: 3).toString()
        budgetThreshold = (settings?.budgetAlertThreshold ?: 80).toString()
        monthlyDay = (settings?.monthlyReminderDay ?: 1).toString()
        themeMode = uiState.themeMode
        themeAccent = uiState.themeAccent
        lockEnabled = uiState.lockEnabled
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("通知 / 表示設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("通知", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("通知を有効化")
                        Checkbox(checked = enabled, onCheckedChange = { enabled = it })
                    }
                    OutlinedTextField(
                        value = leadDays,
                        onValueChange = { if (it.all(Char::isDigit)) leadDays = it },
                        label = { Text("引落N日前") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = budgetThreshold,
                        onValueChange = { if (it.all(Char::isDigit)) budgetThreshold = it },
                        label = { Text("予算アラート(%)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = monthlyDay,
                        onValueChange = { if (it.all(Char::isDigit)) monthlyDay = it },
                        label = { Text("月次催促日(1-28)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("表示テーマ", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("system" to "自動", "light" to "ライト", "dark" to "ダーク").forEach { (value, label) ->
                            FilterChip(
                                selected = themeMode == value,
                                onClick = { themeMode = value },
                                label = { Text(label) }
                            )
                        }
                    }
                    Text("アクセントカラー")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "ocean" to "Ocean",
                            "emerald" to "Emerald",
                            "sunset" to "Sunset",
                            "slate" to "Slate"
                        ).forEach { (value, label) ->
                            FilterChip(
                                selected = themeAccent == value,
                                onClick = { themeAccent = value },
                                label = { Text(label) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("起動ロック（生体認証）")
                        Checkbox(checked = lockEnabled, onCheckedChange = { lockEnabled = it })
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.save(
                        settings = NotificationSettingEntity(
                            id = uiState.settings?.id ?: 0,
                            enabled = enabled,
                            reminderLeadDays = leadDays.toIntOrNull()?.coerceAtLeast(0) ?: 3,
                            budgetAlertThreshold = budgetThreshold.toIntOrNull()?.coerceIn(10, 200) ?: 80,
                            monthlyReminderDay = monthlyDay.toIntOrNull()?.coerceIn(1, 28) ?: 1
                        ),
                        themeMode = themeMode,
                        themeAccent = themeAccent,
                        lockEnabled = lockEnabled
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
            Text(
                "テーマ変更は保存後すぐ反映されます。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

