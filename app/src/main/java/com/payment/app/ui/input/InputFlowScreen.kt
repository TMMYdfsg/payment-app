package com.payment.app.ui.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.model.CardWithPayment
import com.payment.app.ui.components.AccountSelector
import com.payment.app.ui.components.formatCurrency
import com.payment.app.ui.theme.getDueDateColor
import com.payment.app.util.asDisplayLabel
import com.payment.app.util.calculateBillingDate
import com.payment.app.util.parseYearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFlowScreen(
    dueDate: Int?,
    yearMonth: String?,
    onNavigateBack: () -> Unit,
    onNavigateToList: (String) -> Unit,
    viewModel: InputFlowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val month = parseYearMonth(yearMonth)

    LaunchedEffect(dueDate, yearMonth) {
        viewModel.initializeForDueDate(dueDate, yearMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        buildString {
                            append(month.asDisplayLabel())
                            append(" ")
                            append(if (dueDate != null) "${dueDate}日入力" else "全カード入力")
                        }
                    )
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.showSummary -> {
                SummaryContent(
                    cards = uiState.cards,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onDone = onNavigateBack,
                    onViewList = { onNavigateToList(uiState.selectedYearMonth) }
                )
            }

            else -> {
                val currentCard = uiState.cards.getOrNull(uiState.currentIndex)
                if (currentCard != null) {
                    InputContent(
                        currentCard = currentCard,
                        accountsCount = uiState.accounts.size,
                        currentIndex = uiState.currentIndex,
                        totalCards = uiState.cards.size,
                        inputText = uiState.inputText,
                        onInputChange = viewModel::onInputChange,
                        onConfirm = viewModel::onConfirm,
                        onSkip = viewModel::onSkip,
                        onTogglePaid = viewModel::toggleCurrentPaid,
                        accountSelector = {
                            AccountSelector(
                                accounts = uiState.accounts,
                                selectedAccountId = currentCard.accountId,
                                selectedLabel = currentCard.accountName,
                                onSelected = viewModel::selectCurrentAccount
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun InputContent(
    currentCard: CardWithPayment,
    accountsCount: Int,
    currentIndex: Int,
    totalCards: Int,
    inputText: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit,
    onTogglePaid: () -> Unit,
    accountSelector: @Composable (() -> Unit),
    modifier: Modifier = Modifier
) {
    val color = getDueDateColor(currentCard.dueDate)
    val billingInfo = calculateBillingDate(parseYearMonth(currentCard.yearMonth), currentCard.dueDate)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCards.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
        Text("${currentIndex + 1} / $totalCards")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                Text(
                    "予定引落日: ${billingInfo.scheduledDate.asDisplayLabel()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (billingInfo.requestedDate != billingInfo.scheduledDate) {
                    Text(
                        "指定日 ${billingInfo.requestedDate.asDisplayLabel()} は休日のため翌営業日に調整",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (currentCard.amount > 0) {
                    Text(
                        "登録額: ${formatCurrency(currentCard.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("金額") },
            prefix = { Text("¥") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (accountsCount > 0) {
            accountSelector()
        }

        FilterChip(
            selected = currentCard.isPaid,
            onClick = onTogglePaid,
            label = { Text(if (currentCard.isPaid) "引き落とし完了" else "未完了") }
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
                Text("確定")
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
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "📊 合計金額",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        grouped.entries.sortedBy { it.key }.forEach { (dueDate, cardList) ->
            val color = getDueDateColor(dueDate)
            val subtotal = cardList.sumOf { it.amount }

            Text(
                "【${dueDate}日】",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            cardList.forEach { card ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(card.cardName)
                        Text(
                            "${card.accountName ?: "口座未設定"} / ${if (card.isPaid) "完了" else "未完了"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(formatCurrency(card.amount))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("小計", fontWeight = FontWeight.Bold)
                Text(formatCurrency(subtotal), fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("💰 総額", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                formatCurrency(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
