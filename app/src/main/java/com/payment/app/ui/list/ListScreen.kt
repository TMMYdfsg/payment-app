package com.payment.app.ui.list

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.model.CardWithPayment
import com.payment.app.ui.components.AccountSelector
import com.payment.app.ui.components.MonthSwitcher
import com.payment.app.ui.components.SummaryMetricCard
import com.payment.app.ui.components.formatCurrency
import com.payment.app.ui.theme.getDueDateColor
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.parseYearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    yearMonth: String?,
    onNavigateBack: () -> Unit,
    onNavigateToAccountManage: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingCard by remember { mutableStateOf<CardWithPayment?>(null) }
    var editAmount by remember { mutableStateOf("") }

    LaunchedEffect(yearMonth) {
        viewModel.setYearMonth(yearMonth)
    }

    editingCard?.let { card ->
        AlertDialog(
            onDismissRequest = { editingCard = null },
            title = { Text(card.cardName) },
            text = {
                OutlinedTextField(
                    value = editAmount,
                    onValueChange = { if (it.all { c -> c.isDigit() }) editAmount = it },
                    label = { Text("金額") },
                    prefix = { Text("¥") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateAmount(card.cardId, editAmount.toLongOrNull() ?: 0L)
                        editingCard = null
                    }
                ) {
                    Text("更新")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCard = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("支払い一覧", fontWeight = FontWeight.ExtraBold)
                        Text(
                            uiState.monthLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateToAccountManage) {
                        Text("口座")
                    }
                }
            )
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
            return@Scaffold
        }

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
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    label = { Text("履歴検索（カード/カテゴリ/口座/月/金額）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                AnimatedContent(
                    targetState = uiState.totalAmount to uiState.unpaidAmount,
                    label = "list_summary_anim"
                ) { (totalAmount, unpaidAmount) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SummaryMetricCard(
                            title = "総額",
                            value = formatCurrency(totalAmount),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "未完了",
                            value = formatCurrency(unpaidAmount),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (uiState.searchQuery.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("検索結果（全月）", fontWeight = FontWeight.Bold)
                            if (uiState.historyResults.isEmpty()) {
                                Text("一致する履歴がありません")
                            } else {
                                uiState.historyResults.take(40).forEach { result ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(result.cardName, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${parseYearMonth(result.yearMonth).asDisplayLabel()} / ${result.dueDate}日 / ${result.accountName ?: "口座未設定"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(formatCurrency(result.amount))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            uiState.cardsByDueDate.entries.sortedBy { it.key }.forEach { (dueDate, cards) ->
                val filtered = cards.filter {
                    uiState.searchQuery.isBlank() ||
                        it.cardName.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.category.contains(uiState.searchQuery, ignoreCase = true) ||
                        (it.accountName ?: "").contains(uiState.searchQuery, ignoreCase = true)
                }
                if (filtered.isEmpty()) return@forEach
                item {
                    val color = getDueDateColor(dueDate)
                    val subtotal = filtered.sumOf { it.amount }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            filtered.forEach { card ->
                                key(card.cardId) {
                                    val dismissState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { value ->
                                            when (value) {
                                                SwipeToDismissBoxValue.StartToEnd -> {
                                                    viewModel.updatePaid(card.cardId, !card.isPaid)
                                                    false
                                                }

                                                SwipeToDismissBoxValue.EndToStart -> {
                                                    viewModel.deletePayment(card.cardId)
                                                    false
                                                }

                                                SwipeToDismissBoxValue.Settled -> true
                                            }
                                        },
                                        positionalThreshold = { it * 0.30f }
                                    )
                                    SwipeToDismissBox(
                                        state = dismissState,
                                        enableDismissFromStartToEnd = true,
                                        enableDismissFromEndToStart = true,
                                        backgroundContent = {
                                            val target = dismissState.targetValue
                                            val isDelete = target == SwipeToDismissBoxValue.EndToStart
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        if (isDelete) MaterialTheme.colorScheme.errorContainer
                                                        else MaterialTheme.colorScheme.primaryContainer
                                                    )
                                                    .padding(horizontal = 16.dp),
                                                horizontalArrangement = if (isDelete) Arrangement.End else Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    if (isDelete) "削除" else if (card.isPaid) "未完了に戻す" else "引落完了",
                                                    color = if (isDelete) MaterialTheme.colorScheme.onErrorContainer
                                                    else MaterialTheme.colorScheme.onPrimaryContainer,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        PaymentRow(
                                            card = card,
                                            selectedYearMonth = uiState.selectedYearMonth,
                                            accounts = uiState.accounts,
                                            onEditAmount = {
                                                editingCard = card
                                                editAmount = card.amount.takeIf { it > 0 }?.toString().orEmpty()
                                            },
                                            onTogglePaid = { viewModel.updatePaid(card.cardId, !card.isPaid) },
                                            onSelectAccount = { viewModel.updateAccount(card.cardId, it) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(
    card: CardWithPayment,
    selectedYearMonth: String,
    accounts: List<com.payment.app.data.db.entity.BankAccountEntity>,
    onEditAmount: () -> Unit,
    onTogglePaid: () -> Unit,
    onSelectAccount: (Long?) -> Unit
) {
    val billingInfo = calculateBillingDate(parseYearMonth(selectedYearMonth), card.dueDate)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEditAmount)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        card.cardName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (card.category.isNotBlank()) {
                        Text(
                            card.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "予定日: ${billingInfo.scheduledDate.asDisplayLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    formatCurrency(card.amount),
                    fontWeight = if (card.amount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }
            AccountSelector(
                accounts = accounts,
                selectedAccountId = card.accountId,
                selectedLabel = card.accountName,
                onSelected = onSelectAccount
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onTogglePaid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (card.isPaid) "未完了に戻す" else "引落完了")
                }
                Button(
                    onClick = onEditAmount,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("金額編集")
                }
            }
        }
    }
}
