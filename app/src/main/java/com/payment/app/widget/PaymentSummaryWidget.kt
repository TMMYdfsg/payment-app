package com.payment.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.SizeMode
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.LocalSize
import com.payment.app.data.repository.PaymentRepository
import com.payment.app.navigation.AppLaunchRoute
import com.payment.app.navigation.createLaunchIntent
import com.payment.app.util.asStorageKey
import com.payment.app.util.currentYearMonth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

private data class WidgetSnapshot(
    val month: String,
    val total: Long,
    val unpaidCount: Int,
    val paidCount: Int,
    val totalCount: Int,
    val nextDueLabel: String,
    val nextDueDate: Int?
)

class PaymentSummaryWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            androidx.compose.ui.unit.DpSize(140.dp, 140.dp),
            androidx.compose.ui.unit.DpSize(200.dp, 140.dp),
            androidx.compose.ui.unit.DpSize(240.dp, 200.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = entryPoint(context).repository()
        val yearMonth = currentYearMonth().asStorageKey()
        val cards = repository.getCardPaymentsOnce(yearMonth)
        val total = cards.sumOf { it.amount }
        val unpaidCount = cards.count { !it.isPaid && it.amount > 0L }
        val paidCount = cards.count { it.isPaid && it.amount > 0L }
        val nextDueCard = cards
            .filter { !it.isPaid && it.amount > 0L }
            .minByOrNull { it.dueDate }
        val nextDue = nextDueCard?.let { "${it.dueDate}日 ${it.cardName}" } ?: "未完了なし"

        provideContent {
            PaymentSummaryContent(
                snapshot = WidgetSnapshot(
                    month = yearMonth,
                    total = total,
                    unpaidCount = unpaidCount,
                    paidCount = paidCount,
                    totalCount = cards.count { it.amount > 0L },
                    nextDueLabel = nextDue,
                    nextDueDate = nextDueCard?.dueDate
                )
            )
        }
    }

    private fun entryPoint(context: Context): WidgetEntryPoint =
        EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
}

@Composable
private fun PaymentSummaryContent(snapshot: WidgetSnapshot) {
    val context = LocalContext.current
    val openHome = actionStartActivity(createLaunchIntent(context, AppLaunchRoute.home()))
    val openNextDue = actionStartActivity(
        createLaunchIntent(
            context,
            snapshot.nextDueDate?.let { AppLaunchRoute.inputForDueDate(it) } ?: AppLaunchRoute.input()
        )
    )
    val openList = actionStartActivity(createLaunchIntent(context, AppLaunchRoute.list()))
    val openUnpaid = actionStartActivity(createLaunchIntent(context, AppLaunchRoute.listUnpaid()))
    val openCalendar = actionStartActivity(createLaunchIntent(context, AppLaunchRoute.calendar()))
    val progressText = if (snapshot.totalCount > 0) {
        "${((snapshot.paidCount * 100f) / snapshot.totalCount.toFloat()).toInt()}%"
    } else {
        "0%"
    }
    val size = LocalSize.current

    when {
        size.width <= 150.dp -> SmallLayout(snapshot, progressText, openHome)
        size.height <= 150.dp -> MediumLayout(snapshot, progressText, openHome, openNextDue, openUnpaid)
        else -> LargeLayout(snapshot, progressText, openHome, openNextDue, openList, openUnpaid, openCalendar)
    }
}

@Composable
private fun SmallLayout(snapshot: WidgetSnapshot, progressText: String, openHome: androidx.glance.action.Action) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF2F6FF)))
            .clickable(openHome)
            .padding(10.dp),
        verticalAlignment = Alignment.Vertical.Top
    ) {
        Text("¥${snapshot.total}", style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF0B3D91))))
        Text("未 ${snapshot.unpaidCount} / 完 $progressText", style = TextStyle(color = ColorProvider(Color(0xFF4A5566))))
    }
}

@Composable
private fun MediumLayout(
    snapshot: WidgetSnapshot,
    progressText: String,
    openHome: androidx.glance.action.Action,
    openNextDue: androidx.glance.action.Action,
    openUnpaid: androidx.glance.action.Action
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF2F6FF)))
            .clickable(openHome)
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.Top
    ) {
        Text("¥${snapshot.total}", style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF0B3D91))))
        Text("未 ${snapshot.unpaidCount}件 / 完了率 $progressText", style = TextStyle(color = ColorProvider(Color(0xFF4A5566))))
        Text("次: ${snapshot.nextDueLabel}", style = TextStyle(color = ColorProvider(Color(0xFF4A5566))))
        Spacer(modifier = GlanceModifier.height(8.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetActionChip("次へ", openNextDue)
            Spacer(modifier = GlanceModifier.width(6.dp))
            WidgetActionChip("未完了", openUnpaid)
        }
    }
}

@Composable
private fun LargeLayout(
    snapshot: WidgetSnapshot,
    progressText: String,
    openHome: androidx.glance.action.Action,
    openNextDue: androidx.glance.action.Action,
    openList: androidx.glance.action.Action,
    openUnpaid: androidx.glance.action.Action,
    openCalendar: androidx.glance.action.Action
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF2F6FF)))
            .clickable(openHome)
            .padding(14.dp),
        verticalAlignment = Alignment.Vertical.Top
    ) {
        Text(
            text = "支払い管理 ${snapshot.month}",
            style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF113355)))
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text("総額 ¥${snapshot.total}", style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF0B3D91))))
        Text("未 ${snapshot.unpaidCount}件 / 完 $progressText", style = TextStyle(color = ColorProvider(Color(0xFF036C55))))
        Text("次回引落: ${snapshot.nextDueLabel}", style = TextStyle(color = ColorProvider(Color(0xFF4A5566))))
        Spacer(modifier = GlanceModifier.height(10.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetActionChip("ホーム", openHome)
            Spacer(modifier = GlanceModifier.width(6.dp))
            WidgetActionChip("次へ", openNextDue)
            Spacer(modifier = GlanceModifier.width(6.dp))
            WidgetActionChip("一覧", openList)
            Spacer(modifier = GlanceModifier.width(6.dp))
            WidgetActionChip("未完了", openUnpaid)
        }
        Spacer(modifier = GlanceModifier.height(6.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetActionChip("予定", openCalendar)
        }
    }
}

@Composable
private fun WidgetActionChip(label: String, action: androidx.glance.action.Action) {
    Text(
        text = label,
        modifier = GlanceModifier
            .background(ColorProvider(Color(0xFFD8E8FF)))
            .clickable(action)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            color = ColorProvider(Color(0xFF0B3D91))
        )
    )
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
