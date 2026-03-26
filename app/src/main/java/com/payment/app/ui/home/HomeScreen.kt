package com.payment.app.ui.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.model.CardWithPayment
import com.payment.app.ui.components.formatCurrency
import com.payment.app.util.asDisplayLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInput: (Int?, String) -> Unit,
    onNavigateToList: (String) -> Unit,
    onNavigateToUnpaidList: (String) -> Unit,
    onNavigateToCardManage: () -> Unit,
    onNavigateToAccountManage: () -> Unit,
    onNavigateToAnalytics: (String) -> Unit,
    onNavigateToYearlySummary: (String) -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showDriveDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var pendingBackupJson by remember { mutableStateOf<String?>(null) }
    var driveToken by remember { mutableStateOf("") }
    var driveFolderId by remember { mutableStateOf("") }
    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingBackupJson
        if (uri != null && json != null) {
            scope.launch {
                val saved = viewModel.saveBackupJson(uri, json)
                exportMessage = if (saved) "JSONバックアップを保存しました" else "JSONバックアップ保存に失敗しました"
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("月次データをリセット") },
            text = { Text("${uiState.monthLabel} の金額と完了状態をリセットします。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSelectedMonth()
                        showResetDialog = false
                    }
                ) { Text("リセット") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("キャンセル") }
            }
        )
    }

    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("月予算を設定") },
            text = {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { if (it.all(Char::isDigit)) budgetInput = it },
                    label = { Text("金額 (¥)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateBudget(budgetInput.toLongOrNull() ?: 0L)
                        showBudgetDialog = false
                    },
                    enabled = budgetInput.isNotBlank()
                ) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("キャンセル") }
            }
        )
    }

    if (showDriveDialog) {
        AlertDialog(
            onDismissRequest = { showDriveDialog = false },
            title = { Text("Google Drive 同期") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = driveToken,
                        onValueChange = { driveToken = it },
                        label = { Text("アクセストークン") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = driveFolderId,
                        onValueChange = { driveFolderId = it },
                        label = { Text("フォルダID (任意)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Drive REST API v3 を利用してバックアップJSONをアップロードします。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val json = viewModel.buildBackupJson()
                            val fileName = "payment_backup_${uiState.selectedYearMonth}_${System.currentTimeMillis()}.json"
                            val result = viewModel.uploadBackupToDrive(
                                accessToken = driveToken,
                                folderId = driveFolderId.ifBlank { null },
                                fileName = fileName,
                                json = json
                            )
                            exportMessage = result.fold(
                                onSuccess = { "Drive同期完了: fileId=$it" },
                                onFailure = { "Drive同期失敗: ${it.message}" }
                            )
                        }
                        showDriveDialog = false
                    },
                    enabled = driveToken.isNotBlank()
                ) { Text("同期") }
            },
            dismissButton = {
                TextButton(onClick = { showDriveDialog = false }) { Text("キャンセル") }
            }
        )
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.surface) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val highlightCards = uiState.cardsByDueDate.values.flatten()
            .sortedByDescending { it.amount }
            .take(4)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            uiState.monthLabel,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "家計ダッシュボード",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(onClick = onNavigateToCalendar) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "カレンダー")
                        }
                        IconButton(onClick = onNavigateToNotification, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Default.Notifications, contentDescription = "通知設定", modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = { onNavigateToList(uiState.selectedYearMonth) }) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "一覧")
                        }
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = "その他")
                        }
                    }
                }
            }

            item {
                HeroCard(
                    totalAmount = uiState.totalAmount,
                    remaining = uiState.budgetRemaining,
                    onPrevious = { viewModel.changeMonth(-1) },
                    onNext = { viewModel.changeMonth(1) }
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { onNavigateToInput(null, uiState.selectedYearMonth) },
                        modifier = Modifier.weight(1f)
                    ) { Text("全カード入力") }
                    FilledTonalButton(
                        onClick = { viewModel.markAllPaid() },
                        modifier = Modifier.weight(1f)
                    ) { Text("今月を一括完了") }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val applied = viewModel.applyPreviousMonthTemplate()
                                exportMessage = if (applied > 0) {
                                    "固定費テンプレを適用しました（${applied}件）"
                                } else {
                                    "適用できるテンプレがありませんでした"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("固定費テンプレ適用") }
                    OutlinedButton(
                        onClick = { onNavigateToUnpaidList(uiState.selectedYearMonth) },
                        modifier = Modifier.weight(1f)
                    ) { Text("未完了一覧") }
                }
            }

            if (uiState.nextActions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("次アクション", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (uiState.overdueActionCount > 0) {
                                Text(
                                    "期限超過 ${uiState.overdueActionCount}件",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            uiState.nextActions.forEach { action ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToInput(action.dueDate, uiState.selectedYearMonth) },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(action.cardName, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "予定日 ${action.scheduledDate.asDisplayLabel()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(formatCurrency(action.amount), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onNavigateToAnalytics(uiState.selectedYearMonth) },
                        modifier = Modifier.weight(1f)
                    ) { Text("分析") }
                    OutlinedButton(
                        onClick = { onNavigateToYearlySummary(uiState.selectedYearMonth) },
                        modifier = Modifier.weight(1f)
                    ) { Text("年間サマリー") }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onNavigateToSubscription,
                        modifier = Modifier.weight(1f)
                    ) { Text("サブスク/分割") }
                    OutlinedButton(
                        onClick = onNavigateToCardManage,
                        modifier = Modifier.weight(1f)
                    ) { Text("カード管理") }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("支払い一覧", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onNavigateToList(uiState.selectedYearMonth) }) {
                        Text("履歴を見る")
                    }
                }
            }

            if (highlightCards.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "データがありません",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(highlightCards) { card ->
                    HomePaymentCard(
                        card = card,
                        onClick = { onNavigateToInput(card.dueDate, uiState.selectedYearMonth) }
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniMetricCard(
                        title = "貯蓄率",
                        value = buildSavingsRate(uiState.totalAmount, uiState.budgetAmount),
                        modifier = Modifier.weight(1f)
                    )
                    MiniMetricCard(
                        title = "スコア",
                        value = "${buildScore(uiState.paidCount, uiState.totalCount)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("運用アクション", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val file = viewModel.exportCurrentMonth()
                                        if (file == null) {
                                            exportMessage = "データがありません"
                                            return@launch
                                        }
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/csv"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            putExtra(Intent.EXTRA_SUBJECT, "payments_${uiState.selectedYearMonth}.csv")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "CSVを共有"))
                                        exportMessage = "CSV共有シートを開きました"
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("CSV共有") }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        pendingBackupJson = viewModel.buildBackupJson()
                                        backupLauncher.launch("payment_backup_${uiState.selectedYearMonth}.json")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("JSON保存") }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showDriveDialog = true },
                                modifier = Modifier.weight(1f)
                            ) { Text("Drive同期") }
                            OutlinedButton(
                                onClick = { viewModel.rescheduleNotifications() },
                                modifier = Modifier.weight(1f)
                            ) { Text("通知再設定") }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    budgetInput = uiState.budgetAmount?.toString().orEmpty()
                                    showBudgetDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("予算設定") }
                            OutlinedButton(
                                onClick = onNavigateToAccountManage,
                                modifier = Modifier.weight(1f)
                            ) { Text("口座管理") }
                        }
                        if (!exportMessage.isNullOrBlank()) {
                            Text(
                                exportMessage.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    totalAmount: Long,
    remaining: Long?,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "月間支払い総額",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f)
                )
                Text(
                    formatCurrency(totalAmount),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "残金 ${formatCurrency(remaining ?: 0L)}",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SuggestionChip(
                    onClick = onPrevious,
                    label = { Icon(Icons.Default.ChevronLeft, contentDescription = "前月") }
                )
                SuggestionChip(
                    onClick = onNext,
                    label = { Icon(Icons.Default.ChevronRight, contentDescription = "翌月") }
                )
            }
        }
    }
}

@Composable
private fun HomePaymentCard(
    card: CardWithPayment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(card.cardName, fontWeight = FontWeight.Bold)
                    Text(
                        "${card.dueDate}日 引落予定",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatCurrency(card.amount), fontWeight = FontWeight.ExtraBold)
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = if (card.isPaid) Color(0xFFE0F7ED) else Color(0xFFFFE6E2)
                ) {
                    Text(
                        if (card.isPaid) "完了" else "未完了",
                        color = if (card.isPaid) Color(0xFF036C55) else Color(0xFFBA1A1A),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

private fun buildSavingsRate(total: Long, budget: Long?): String {
    if (budget == null || budget <= 0L) return "--"
    val ratio = ((budget - total).coerceAtLeast(0L) * 100f / budget.toFloat())
    return String.format("%.1f%%", ratio)
}

private fun buildScore(paidCount: Int, totalCount: Int): Int {
    if (totalCount <= 0) return 500
    val completion = paidCount.toFloat() / totalCount.toFloat()
    return (600 + completion * 300).toInt()
}
