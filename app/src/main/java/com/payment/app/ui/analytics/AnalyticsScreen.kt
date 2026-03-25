package com.payment.app.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.ui.components.MonthSwitcher
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
                title = { Text(uiState.monthLabel) },
                navigationIcon = { Icon(Icons.Default.AutoGraph, contentDescription = null, modifier = Modifier.padding(start = 12.dp)) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
        val budget = uiState.budgetAmount ?: 0L
        val budgetRemaining = (budget - totalThisMonth).coerceAtLeast(0L)
        val categoryMax = uiState.categoryBreakdown.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 1L

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                MonthSwitcher(
                    label = uiState.monthLabel,
                    onPrevious = { viewModel.changeMonth(-1) },
                    onNext = { viewModel.changeMonth(1) }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "支出総額",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Text(
                            formatCurrency(totalThisMonth),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        val max = uiState.monthlyTotals.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 1L
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.monthlyTotals.takeLast(8).forEach { (_, amount) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(58.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.65f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .height((56f * (amount.toFloat() / max.toFloat()).coerceIn(0.06f, 1f)).dp)
                                            .background(
                                                if (amount == totalThisMonth) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                                                }
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("カテゴリ別内訳", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (uiState.categoryBreakdown.isEmpty()) {
                item { Text("データなし", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(uiState.categoryBreakdown) { (category, amount) ->
                    Card(shape = RoundedCornerShape(18.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(category, fontWeight = FontWeight.SemiBold)
                                Text(formatCurrency(amount), fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = { (amount.toFloat() / categoryMax.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "今月の予算状況",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Text(
                            "${uiState.budgetUsagePercent}%",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "${formatCurrency(totalThisMonth)} / ${formatCurrency(budget)}",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Text(
                            "残予算 ${formatCurrency(budgetRemaining)}",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        LinearProgressIndicator(
                            progress = { (uiState.budgetUsagePercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF9EF3D6),
                            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

