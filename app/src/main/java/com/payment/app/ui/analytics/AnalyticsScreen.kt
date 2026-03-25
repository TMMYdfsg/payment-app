package com.payment.app.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.ui.components.MonthSwitcher
import com.payment.app.ui.components.SummaryMetricCard
import com.payment.app.ui.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    yearMonth: String?,
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(yearMonth) {
        viewModel.setMonth(yearMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分析") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
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

        val totalThisMonth = uiState.monthlyTotals.lastOrNull()?.second ?: 0L
        val cardTotal = uiState.cardBreakdown.sumOf { it.second }.coerceAtLeast(1L)
        val categoryMax = uiState.categoryBreakdown.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 1L
        val budget = uiState.budgetAmount

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MonthSwitcher(
                    label = uiState.monthLabel,
                    onPrevious = { viewModel.changeMonth(-1) },
                    onNext = { viewModel.changeMonth(1) }
                )
            }

            item {
                SummaryMetricCard(
                    title = "当月合計",
                    value = formatCurrency(totalThisMonth),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("過去12ヶ月推移", fontWeight = FontWeight.Bold)
                        val max = uiState.monthlyTotals.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 1L
                        uiState.monthlyTotals.forEach { (ym, amount) ->
                            val progress = (amount.toFloat() / max.toFloat()).coerceIn(0f, 1f)
                            Text("$ym  ${formatCurrency(amount)}", style = MaterialTheme.typography.bodySmall)
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("カテゴリ別支出（棒グラフ）", fontWeight = FontWeight.Bold)
                        if (uiState.categoryBreakdown.isEmpty()) {
                            Text("データなし")
                        } else {
                            uiState.categoryBreakdown.forEach { (category, amount) ->
                                val progress = (amount.toFloat() / categoryMax.toFloat()).coerceIn(0f, 1f)
                                Text("$category  ${formatCurrency(amount)}", style = MaterialTheme.typography.bodySmall)
                                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("カード別内訳（円グラフ）", fontWeight = FontWeight.Bold)
                        if (uiState.cardBreakdown.isEmpty()) {
                            Text("データなし")
                        } else {
                            val palette = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.secondary,
                                Color(0xFFE57373),
                                Color(0xFF81C784),
                                Color(0xFF64B5F6),
                                Color(0xFFFFB74D)
                            )
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                val diameter = size.minDimension * 0.75f
                                val strokeWidth = diameter * 0.22f
                                val topLeftX = (size.width - diameter) / 2f
                                val topLeftY = (size.height - diameter) / 2f
                                var startAngle = -90f
                                uiState.cardBreakdown.forEachIndexed { index, (_, amount) ->
                                    val sweep = (amount.toFloat() / cardTotal.toFloat()) * 360f
                                    drawArc(
                                        color = palette[index % palette.size],
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        topLeft = Offset(topLeftX, topLeftY),
                                        size = Size(diameter, diameter),
                                        style = Stroke(width = strokeWidth)
                                    )
                                    startAngle += sweep
                                }
                            }
                            uiState.cardBreakdown.forEachIndexed { index, (card, amount) ->
                                val percent = ((amount * 100) / cardTotal).toInt()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${index + 1}. $card", color = palette[index % palette.size])
                                    Text("${formatCurrency(amount)} ($percent%)")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("予算消化率ゲージ", fontWeight = FontWeight.Bold)
                        if (budget == null || budget <= 0L) {
                            Text("予算未設定")
                        } else {
                            Text("${uiState.budgetUsagePercent}% / ${formatCurrency(budget)}")
                            LinearProgressIndicator(
                                progress = { (uiState.budgetUsagePercent / 100f).coerceAtMost(1f) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
