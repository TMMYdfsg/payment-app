package com.payment.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.payment.app.ui.components.MonthSwitcher
import com.payment.app.ui.components.SummaryMetricCard
import com.payment.app.ui.components.formatCurrency
import com.payment.app.ui.theme.getDueDateColor
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.exportPaymentsToCsv

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInput: (Int?, String) -> Unit,
    onNavigateToList: (String) -> Unit,
    onNavigateToCardManage: () -> Unit,
    onNavigateToAccountManage: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("月次データをリセット") },
            text = { Text("${uiState.monthLabel} の金額と完了状態をリセットします。カードと口座は保持されます。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSelectedMonth()
                        showResetDialog = false
                    }
                ) {
                    Text("リセット")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("キャンセル")
                }
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
                    onValueChange = { if (it.all { c -> c.isDigit() }) budgetInput = it },
                    label = { Text("金額 (¥)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val value = budgetInput.toLongOrNull() ?: 0L
                        viewModel.updateBudget(value)
                        showBudgetDialog = false
                    },
                    enabled = budgetInput.isNotBlank()
                ) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("支払い管理") },
                actions = {
                    TextButton(onClick = onNavigateToAccountManage) {
                        Text("口座")
                    }
                    IconButton(onClick = { onNavigateToList(uiState.selectedYearMonth) }) {
                        Icon(Icons.Default.List, contentDescription = "一覧")
                    }
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, contentDescription = "リセット")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCardManage) {
                Icon(Icons.Default.Add, contentDescription = "カード追加")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MonthSwitcher(
                        label = uiState.monthLabel,
                        onPrevious = { viewModel.changeMonth(-1L) },
                        onNext = { viewModel.changeMonth(1L) }
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.markAllPaid() },
                            modifier = Modifier.weight(1f)
                        ) { Text("今月を一括で完了") }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val file = exportPaymentsToCsv(
                                        context,
                                        uiState.selectedYearMonth,
                                        viewModel.getCurrentPayments()
                                    )
                                    exportMessage = file?.absolutePath ?: "今月のデータがありません"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("CSVエクスポート") }
                    }
                    exportMessage?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    SummaryMetricCard(
                        title = "月間総額",
                        value = formatCurrency(uiState.totalAmount),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SummaryMetricCard(
                            title = "未完了",
                            value = formatCurrency(uiState.unpaidAmount),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "完了済み",
                            value = "${uiState.paidCount}/${uiState.totalCount}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("月予算", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                TextButton(onClick = {
                                    budgetInput = uiState.budgetAmount?.toString().orEmpty()
                                    showBudgetDialog = true
                                }) { Text("設定") }
                            }
                            val budget = uiState.budgetAmount
                            if (budget != null) {
                                val remaining = uiState.budgetRemaining ?: 0L
                                Text("設定額: ${formatCurrency(budget)}")
                                Text("残り: ${formatCurrency(remaining)}", color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                LinearProgressIndicator(
                                    progress = { (uiState.totalAmount.toFloat() / budget.toFloat()).coerceAtMost(1f) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text("未設定です")
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "カレンダー / 営業日調整",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            uiState.scheduleGroups.forEach { group ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            group.scheduledDate.asDisplayLabel(),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(formatCurrency(group.subtotal))
                                    }
                                    Text(
                                        group.cardNames.joinToString(" / "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (group.adjustedByHoliday) {
                                        Text(
                                            "元の指定日: ${group.requestedDate.asDisplayLabel()} → 祝日/休日のため翌営業日へ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.accountTotals.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "口座別集計",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                uiState.accountTotals.forEach { (accountName, total) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(accountName)
                                        Text(formatCurrency(total), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                items(uiState.dueDates) { dueDate ->
                    val cards = uiState.cardsByDueDate[dueDate].orEmpty()
                    val subtotal = uiState.subtotalByDueDate[dueDate] ?: 0L
                    val color = getDueDateColor(dueDate)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "【${dueDate}日】",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                                Text(
                                    formatCurrency(subtotal),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            cards.forEach { card ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(card.cardName)
                                        if (card.category.isNotBlank()) {
                                            Text(
                                                card.category,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            card.accountName ?: "口座未設定",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    FilterChip(
                                        selected = card.isPaid,
                                        onClick = { },
                                        enabled = false,
                                        label = {
                                            Text(if (card.isPaid) "引落完了" else "未完了")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.dueDates) { dueDate ->
                                val color = getDueDateColor(dueDate)
                                Button(
                                    onClick = { onNavigateToInput(dueDate, uiState.selectedYearMonth) },
                                    colors = ButtonDefaults.buttonColors(containerColor = color)
                                ) {
                                    Text("${dueDate}日を入力")
                                }
                            }
                        }
                        Button(
                            onClick = { onNavigateToInput(null, uiState.selectedYearMonth) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("全カード入力")
                        }
                    }
                }
            }
        }
    }
}
