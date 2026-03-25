package com.payment.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.ui.components.MonthSwitcher
import com.payment.app.ui.components.formatCurrency
import com.payment.app.util.asDisplayLabel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.surface) { paddingValues ->
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

        val selectedTotal = uiState.selectedDateItems.sumOf { it.amount }
        val hasShift = uiState.selectedDateItems.any { it.requestedDate != it.scheduledDate }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                        Column {
                            Text("カレンダー", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text(uiState.monthLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DayOfWeek.values().forEach { dow ->
                                Text(
                                    dow.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            uiState.daysInGrid.chunked(7).forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    week.forEach { date ->
                                        if (date == null) {
                                            Box(modifier = Modifier.weight(1f).height(58.dp))
                                        } else {
                                            val items = uiState.paymentsByDate[date].orEmpty()
                                            val selected = date == uiState.selectedDate
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(58.dp)
                                                    .clickable { viewModel.selectDate(date) },
                                                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                                                    else MaterialTheme.colorScheme.surfaceContainerLowest
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(6.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("${date.dayOfMonth}", style = MaterialTheme.typography.bodySmall)
                                                    if (items.isNotEmpty()) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(MaterialTheme.shapes.small)
                                                                    .background(MaterialTheme.colorScheme.primary)
                                                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                                                            ) {
                                                                Text(
                                                                    "${items.size}",
                                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                                    style = MaterialTheme.typography.labelSmall
                                                                )
                                                            }
                                                            if (items.any { it.requestedDate != it.scheduledDate }) {
                                                                Icon(
                                                                    Icons.Default.KeyboardArrowRight,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(14.dp),
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
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("予定", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "${uiState.selectedDate.asDisplayLabel()} の予定",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("支払合計", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            formatCurrency(selectedTotal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (hasShift) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Text(
                            "銀行休業日により引落日がシフトしています。",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (uiState.selectedDateItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text("この日の引落予定はありません。", modifier = Modifier.padding(14.dp))
                    }
                }
            } else {
                items(uiState.selectedDateItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(item.cardName, fontWeight = FontWeight.Bold)
                            Text("予定額: ${formatCurrency(item.amount)}")
                            if (item.requestedDate != item.scheduledDate) {
                                Text(
                                    "シフト: ${item.requestedDate.asDisplayLabel()} -> ${item.scheduledDate.asDisplayLabel()}",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ホームへ戻る")
                }
            }
        }
    }
}
