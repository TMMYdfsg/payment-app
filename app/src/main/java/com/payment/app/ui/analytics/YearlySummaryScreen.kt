package com.payment.app.ui.analytics

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.ui.components.MonthSwitcher
import com.payment.app.ui.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlySummaryScreen(
    yearMonth: String?,
    onNavigateBack: () -> Unit,
    viewModel: YearlySummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(yearMonth) {
        viewModel.setBaseMonth(yearMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("年間サマリー") },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MonthSwitcher(
                    label = "${uiState.baseMonthLabel} 基準",
                    onPrevious = { viewModel.changeBaseMonth(-1) },
                    onNext = { viewModel.changeBaseMonth(1) }
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("12ヶ月合計", fontWeight = FontWeight.Bold)
                        Text(formatCurrency(uiState.yearlyTotal), style = MaterialTheme.typography.headlineSmall)
                        val paidRatio =
                            if (uiState.yearlyTotal > 0L) uiState.yearlyPaid.toFloat() / uiState.yearlyTotal.toFloat() else 0f
                        Text("完了額: ${formatCurrency(uiState.yearlyPaid)}")
                        LinearProgressIndicator(progress = { paidRatio.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            items(uiState.monthly) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.label, fontWeight = FontWeight.Bold)
                            Text(formatCurrency(item.totalAmount))
                        }
                        val paidRatio = if (item.totalAmount > 0L) item.paidAmount.toFloat() / item.totalAmount.toFloat() else 0f
                        LinearProgressIndicator(progress = { paidRatio.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                        Text(
                            "完了: ${formatCurrency(item.paidAmount)} / 未完了件数: ${item.unpaidCount}/${item.totalCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

