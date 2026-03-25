package com.payment.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class PaymentSummaryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = entryPoint(context).repository()
        val yearMonth = currentYearMonth().asStorageKey()
        val cards = repository.getCardPaymentsOnce(yearMonth)
        val total = cards.sumOf { it.amount }
        val unpaidCount = cards.count { !it.isPaid }

        provideContent {
            PaymentSummaryContent(
                month = yearMonth,
                total = total,
                unpaidCount = unpaidCount
            )
        }
    }

    private fun entryPoint(context: Context): WidgetEntryPoint {
        return EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
    }
}

@Composable
private fun PaymentSummaryContent(
    month: String,
    total: Long,
    unpaidCount: Int
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFE7F2FB)))
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "支払い管理 $month",
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
        Text(text = "総額: ¥$total", modifier = GlanceModifier.padding(top = 8.dp))
        Text(text = "未完了: $unpaidCount 件", modifier = GlanceModifier.padding(top = 6.dp))
    }
}

class PaymentSummaryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PaymentSummaryWidget()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun repository(): PaymentRepository
}

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun refresh() {
        PaymentSummaryWidget().updateAll(context)
    }
}
