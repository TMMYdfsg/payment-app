package com.payment.app.ui.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.db.entity.CardEntity
import com.payment.app.ui.theme.getDueDateColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManageScreen(
    onNavigateBack: () -> Unit,
    viewModel: CardManageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteConfirmCard by remember { mutableStateOf<CardEntity?>(null) }

    if (showAddDialog) {
        AddCardDialog(
            onConfirm = { name, dueDate, category ->
                viewModel.addCard(name, dueDate, category)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    deleteConfirmCard?.let { card ->
        AlertDialog(
            onDismissRequest = { deleteConfirmCard = null },
            title = { Text("削除確認") },
            text = { Text("「${card.cardName}」を削除しますか？\n関連する支払いデータも削除されます。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCard(card)
                        deleteConfirmCard = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("削除") }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmCard = null }) { Text("キャンセル") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("カード管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
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
                        Text(
                            "【${dueDate}日】",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(cards, key = { it.cardId }) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = getDueDateColor(card.dueDate).copy(alpha = 0.08f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(card.cardName, style = MaterialTheme.typography.bodyLarge)
                                    if (card.category.isNotBlank()) {
                                        Text(
                                            "カテゴリ: ${card.category}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { deleteConfirmCard = card }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "削除",
                                        tint = MaterialTheme.colorScheme.error
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

@Composable
private fun AddCardDialog(
    onConfirm: (String, Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var cardName by remember { mutableStateOf("") }
    var dueDateText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("カード追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("カード名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDateText,
                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) dueDateText = it },
                    label = { Text("支払日（例: 26）") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("カテゴリ（任意）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val dueDate = dueDateText.toIntOrNull() ?: 0
                    onConfirm(cardName, dueDate, category)
                },
                enabled = cardName.isNotBlank() && dueDateText.isNotBlank()
            ) { Text("追加") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
