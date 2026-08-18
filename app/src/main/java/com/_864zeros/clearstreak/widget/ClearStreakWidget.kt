package com._864zeros.clearstreak.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import com._864zeros.clearstreak.MainActivity
import com._864zeros.clearstreak.data.SharedStreakStorage

class ClearStreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val storage = SharedStreakStorage(context)
        val streakDays = storage.getPrimaryStreakDays()
        val title = storage.getPrimaryTitle()
        val nextMilestone = storage.getNextMilestoneName()

        provideContent {
            ClearStreakWidgetContent(
                title = title,
                streakDays = streakDays,
                nextMilestone = nextMilestone
            )
        }
    }

    @Composable
    private fun ClearStreakWidgetContent(
        title: String,
        streakDays: Long,
        nextMilestone: String
    ) {
        val sageGreen = Color(0xFF8BA888)
        val creamBg = Color(0xFFF5F2ED)
        val warmWhite = Color(0xFFFDFCFA)
        val charcoal = Color(0xFF2D2D2D)
        val stone = Color(0xFF5C5C5C)

        val openAppIntent = Intent().apply {
            setClassName("com._864zeros.clearstreak", "com._864zeros.clearstreak.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(warmWhite))
                .cornerRadius(16.dp)
                .padding(14.dp)
                .clickable(actionStartActivity(openAppIntent)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        color = ColorProvider(stone),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$streakDays",
                        style = TextStyle(
                            color = ColorProvider(sageGreen),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = if (streakDays == 1L) "day" else "days",
                        style = TextStyle(
                            color = ColorProvider(charcoal),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = "Next: $nextMilestone",
                    style = TextStyle(
                        color = ColorProvider(stone),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
