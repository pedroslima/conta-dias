package com.example.myapplication.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.data.*

class ContaDiasWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = EventRepository(context)
        val appWidgetId = resolveAppWidgetId(context, id)
        val eventId = appWidgetId?.let { repo.loadWidgetEvent(it) }
        val events = repo.loadEvents()
        val now = System.currentTimeMillis()
        val event = if (eventId != null) {
            events.find { it.id == eventId }
        } else {
            events.filter { it.dateMillis > now }.minByOrNull { it.dateMillis }
                ?: events.firstOrNull()
        }

        provideContent {
            GlanceTheme {
                WidgetContent(event, now)
            }
        }
    }
}

@Composable
private fun WidgetContent(event: Event?, now: Long) {
    val col = event?.let { EventColor.fromKey(it.colorKey) }
    val bgColor = col?.containerColor ?: Color(0xFFFFDBC8)
    val textColor = col?.onColor ?: Color(0xFF3B1200)
    val accentColor = col?.mainColor ?: Color(0xFFC56A3E)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(bgColor))
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center,
    ) {
        if (event == null) {
            Text(
                text = "Adicione um evento",
                style = TextStyle(color = ColorProvider(textColor), fontSize = 14.sp),
            )
        } else {
            val unit = bestUnit(now, event.dateMillis)
            val n = diffIn(now, event.dateMillis, unit)
            val future = isFuture(event.dateMillis)

            Column(
                modifier = GlanceModifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = event.emoji, style = TextStyle(fontSize = 24.sp))
                Text(
                    text = fmtNumber(n),
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = unitLabel(unit, n),
                    style = TextStyle(
                        color = ColorProvider(textColor.copy(alpha = 0.75f)),
                        fontSize = 13.sp,
                    ),
                )
                Text(
                    text = event.title,
                    style = TextStyle(
                        color = ColorProvider(textColor.copy(alpha = 0.65f)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Text(
                    text = if (future) "Faltam" else "Faz",
                    style = TextStyle(
                        color = ColorProvider(accentColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

private fun resolveAppWidgetId(context: Context, glanceId: GlanceId): Int? {
    val manager = GlanceAppWidgetManager(context)
    val allIds = AppWidgetManager.getInstance(context)
        .getAppWidgetIds(ComponentName(context, ContaDiasWidgetReceiver::class.java))
    return allIds.firstOrNull { manager.getGlanceIdBy(it) == glanceId }
}
