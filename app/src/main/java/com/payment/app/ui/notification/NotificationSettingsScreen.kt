package com.payment.app.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
    onNavigateToAccountManage: () -> Unit = {},
    onNavigateToCardManage: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
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
                title = { Text("設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        "通知 / 表示 / セキュリティ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "家計の通知設定",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggleRow(
                            icon = Icons.Default.Notifications,
                            title = "通知を有効化",
                            subtitle = "引落前通知・予算通知・催促通知を有効にします",
                            checked = enabled,
                            onCheckedChange = { enabled = it }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1", "3", "5", "7").forEach { days ->
                                AssistChip(
                                    onClick = { leadDays = days },
                                    label = { Text("${days}日前") }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = leadDays,
                            onValueChange = { if (it.all(Char::isDigit)) leadDays = it },
                            label = { Text("引落N日前リマインダー") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("80", "100").forEach { threshold ->
                                FilterChip(
                                    selected = budgetThreshold == threshold,
                                    onClick = { budgetThreshold = threshold },
                                    label = { Text("${threshold}%") }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = budgetThreshold,
                            onValueChange = { if (it.all(Char::isDigit)) budgetThreshold = it },
                            label = { Text("予算アラートしきい値(%)") },
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
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Text("アクセント")
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
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        SettingActionRow(
                            icon = Icons.Default.Person,
                            title = "引き落とし口座の管理",
                            subtitle = "追加・削除・既定口座の更新",
                            onClick = onNavigateToAccountManage
                        )
                        SettingActionRow(
                            icon = Icons.Default.CreditCard,
                            title = "カード管理",
                            subtitle = "カード追加/削除・還元率の確認",
                            onClick = onNavigateToCardManage
                        )
                        SettingActionRow(
                            icon = Icons.Default.Subscriptions,
                            title = "サブスク/分割払い",
                            subtitle = "月額合算と残回数トラッカー",
                            onClick = onNavigateToSubscription
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggleRow(
                            icon = Icons.Default.Lock,
                            title = "起動ロック（生体認証）",
                            subtitle = "起動時と復帰時に認証を要求",
                            checked = lockEnabled,
                            onCheckedChange = { lockEnabled = it }
                        )
                        AssistChip(
                            onClick = { /* reserve for future privacy screen */ },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Savings, contentDescription = null)
                                    Text(
                                        "データ保護は有効",
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            item {
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
                            lockEnabled = lockEnabled,
                            onSaved = onNavigateBack
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("設定を保存")
                }
            }

            item {
                Text(
                    "保存後に通知スケジュールを再構築します。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null)
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}
