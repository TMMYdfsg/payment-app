package com.payment.app.ui.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.payment.app.ui.components.formatCurrency
import com.payment.app.util.currentYearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSubscriptionDialog by remember { mutableStateOf(false) }
    var showInstallmentDialog by remember { mutableStateOf(false) }

    if (showSubscriptionDialog) {
        AddSubscriptionDialog(
            cards = uiState.cards,
            onDismiss = { showSubscriptionDialog = false },
            onConfirm = { cardId, name, amount, billingDay ->
                viewModel.addSubscription(cardId, name, amount, billingDay)
                showSubscriptionDialog = false
            }
        )
    }

    if (showInstallmentDialog) {
        AddInstallmentDialog(
            cards = uiState.cards,
            onDismiss = { showInstallmentDialog = false },
            onConfirm = { cardId, totalAmount, months, startYm ->
                viewModel.addInstallment(cardId, totalAmount, months, startYm)
                showInstallmentDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("サブスク / 分割管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    TextButton(onClick = { showSubscriptionDialog = true }) { Text("サブスク追加") }
                    TextButton(onClick = { showInstallmentDialog = true }) { Text("分割追加") }
                }
            )
        }
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("サブスク月額合計", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(formatCurrency(uiState.monthlySubscriptionTotal), style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }

            item {
                Text("サブスク一覧", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (uiState.subscriptions.isEmpty()) {
                item { Text("登録なし") }
            } else {
                items(uiState.subscriptions, key = { it.subscriptionId }) { sub ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(sub.serviceName, fontWeight = FontWeight.Bold)
                            Text("金額: ${formatCurrency(sub.amount)} / 請求日: ${sub.billingDay}日")
                            Text("カードID: ${sub.cardId}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Text("分割払いトラッカー", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (uiState.installments.isEmpty()) {
                item { Text("登録なし") }
            } else {
                items(uiState.installments, key = { it.installment.installmentId }) { status ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("支払いID: ${status.installment.paymentId}", fontWeight = FontWeight.Bold)
                            Text("総額: ${formatCurrency(status.installment.totalAmount)} / 回数: ${status.installment.totalMonths}")
                            Text("残回数: ${status.remainingMonths} / 残高: ${formatCurrency(status.remainingAmount)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSubscriptionDialog(
    cards: List<com.payment.app.data.db.entity.CardEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Long, Int) -> Unit
) {
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull()?.cardId ?: 0L) }
    var cardMenuExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var billingDay by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("サブスク追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { cardMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    val selectedName = cards.firstOrNull { it.cardId == selectedCardId }?.cardName ?: "カード選択"
                    Text(selectedName)
                }
                DropdownMenu(expanded = cardMenuExpanded, onDismissRequest = { cardMenuExpanded = false }) {
                    cards.forEach { card ->
                        DropdownMenuItem(
                            text = { Text(card.cardName) },
                            onClick = {
                                selectedCardId = card.cardId
                                cardMenuExpanded = false
                            }
                        )
                    }
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("サービス名") }, singleLine = true)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all(Char::isDigit)) amount = it },
                    label = { Text("金額") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = billingDay,
                    onValueChange = { if (it.all(Char::isDigit)) billingDay = it },
                    label = { Text("請求日(1-31)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        selectedCardId,
                        name,
                        amount.toLongOrNull() ?: 0L,
                        billingDay.toIntOrNull() ?: 1
                    )
                },
                enabled = selectedCardId > 0L && name.isNotBlank() && amount.isNotBlank() && billingDay.isNotBlank()
            ) { Text("追加") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } }
    )
}

@Composable
private fun AddInstallmentDialog(
    cards: List<com.payment.app.data.db.entity.CardEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long, Int, String) -> Unit
) {
    var selectedCardId by remember { mutableStateOf(cards.firstOrNull()?.cardId ?: 0L) }
    var cardMenuExpanded by remember { mutableStateOf(false) }
    var totalAmount by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var startYm by remember { mutableStateOf(currentYearMonth().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("分割追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { cardMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    val selectedName = cards.firstOrNull { it.cardId == selectedCardId }?.cardName ?: "カード選択"
                    Text(selectedName)
                }
                DropdownMenu(expanded = cardMenuExpanded, onDismissRequest = { cardMenuExpanded = false }) {
                    cards.forEach { card ->
                        DropdownMenuItem(
                            text = { Text(card.cardName) },
                            onClick = {
                                selectedCardId = card.cardId
                                cardMenuExpanded = false
                            }
                        )
                    }
                }
                OutlinedTextField(
                    value = totalAmount,
                    onValueChange = { if (it.all(Char::isDigit)) totalAmount = it },
                    label = { Text("総額") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = months,
                    onValueChange = { if (it.all(Char::isDigit)) months = it },
                    label = { Text("総回数") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = startYm,
                    onValueChange = { startYm = it },
                    label = { Text("開始月(yyyy-MM)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        selectedCardId,
                        totalAmount.toLongOrNull() ?: 0L,
                        months.toIntOrNull() ?: 0,
                        startYm
                    )
                },
                enabled = selectedCardId > 0L && totalAmount.isNotBlank() && months.isNotBlank() && startYm.isNotBlank()
            ) { Text("追加") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } }
    )
}
