package com.payment.app.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.model.CardWithPayment
import com.payment.app.ui.components.formatCurrency
import com.payment.app.ui.theme.getDueDateColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onNavigateBack: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingCard by remember { mutableStateOf<CardWithPayment?>(null) }
    var editAmount by remember { mutableStateOf("") }

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
                TextButton(onClick = {
                    val amount = editAmount.toLongOrNull() ?: 0L
                    viewModel.updateAmount(card.cardId, amount)
                    editingCard = null
                }) { Text("更新") }
            },
            dismissButton = {
                TextButton(onClick = { editingCard = null }) { Text("キャンセル") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("金額一覧") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.cardsByDueDate.entries.sortedBy { it.key }.forEach { (dueDate, cards) ->
                    item {
                        val color = getDueDateColor(dueDate)
                        val subtotal = uiState.subtotalByDueDate[dueDate] ?: 0L
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
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
                                        "小計: ${formatCurrency(subtotal)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                cards.forEach { card ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                editingCard = card
                                                editAmount = if (card.amount > 0) card.amount.toString() else ""
                                            }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(card.cardName, style = MaterialTheme.typography.bodyLarge)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                formatCurrency(card.amount),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = if (card.amount > 0) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                " ✏️",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(thickness = 2.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "💰 総額",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatCurrency(uiState.totalAmount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
