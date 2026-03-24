package com.payment.app.ui.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.model.CardWithPayment
import com.payment.app.ui.components.formatCurrency
import com.payment.app.ui.theme.getDueDateColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFlowScreen(
    dueDate: Int?,
    onNavigateBack: () -> Unit,
    onNavigateToList: () -> Unit,
    viewModel: InputFlowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dueDate) {
        viewModel.initializeForDueDate(dueDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (dueDate != null) "${dueDate}日 入力" else "全カード入力")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.showSummary -> {
                SummaryContent(
                    cards = uiState.cards,
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onDone = onNavigateBack,
                    onViewList = onNavigateToList
                )
            }
            else -> {
                val currentCard = uiState.cards.getOrNull(uiState.currentIndex)
                if (currentCard != null) {
                    InputContent(
                        currentCard = currentCard,
                        currentIndex = uiState.currentIndex,
                        totalCards = uiState.cards.size,
                        inputText = uiState.inputText,
                        onInputChange = viewModel::onInputChange,
                        onConfirm = viewModel::onConfirm,
                        onSkip = viewModel::onSkip,
                        modifier = Modifier.fillMaxSize().padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun InputContent(
    currentCard: CardWithPayment,
    currentIndex: Int,
    totalCards: Int,
    inputText: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = getDueDateColor(currentCard.dueDate)

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCards.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
        Text(
            "${currentIndex + 1} / $totalCards",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "【${currentCard.dueDate}日】",
                    style = MaterialTheme.typography.titleSmall,
                    color = color
                )
                Text(
                    currentCard.cardName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (currentCard.amount > 0) {
                    Text(
                        "前回: ${formatCurrency(currentCard.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Amount input
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("金額") },
            prefix = { Text("¥") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f)
            ) {
                Text("スキップ")
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = inputText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text("確定 →")
            }
        }
    }
}

@Composable
private fun SummaryContent(
    cards: List<CardWithPayment>,
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
    onViewList: () -> Unit
) {
    val grouped = cards.groupBy { it.dueDate }
    val total = cards.sumOf { it.amount }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            "📊 合計金額",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        grouped.entries.sortedBy { it.key }.forEach { (dueDate, cardList) ->
            val color = getDueDateColor(dueDate)
            val subtotal = cardList.sumOf { it.amount }

            Text(
                "【${dueDate}日】",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            cardList.forEach { card ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 2.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(card.cardName)
                    Text(formatCurrency(card.amount))
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("小計:", fontWeight = FontWeight.Bold)
                Text(formatCurrency(subtotal), fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("💰 総額:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                formatCurrency(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f)) {
                Text("ホームへ")
            }
            Button(onClick = onViewList, modifier = Modifier.weight(1f)) {
                Text("一覧を見る")
            }
        }
    }
}