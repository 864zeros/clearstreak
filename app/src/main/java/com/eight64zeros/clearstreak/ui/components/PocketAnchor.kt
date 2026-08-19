package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.HapticEngine
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

/**
 * Tactile Pocket Anchor (blueprint §2): a dual-pulse every 60s (micro-time-
 * chunking), a distinct midpoint accent, and a resolving wave on completion.
 *
 * This in-app version runs while the screen is on. The screen-off / background
 * mode (a foreground service) is a planned follow-up — see PROGRESS.md.
 */
@Composable
fun PocketAnchor(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var running by remember { mutableStateOf(false) }
    var durationMin by remember { mutableStateOf(3) }
    var elapsedSec by remember { mutableStateOf(0) }
    val totalSec = durationMin * 60

    LaunchedEffect(running) {
        if (running) {
            elapsedSec = 0
            HapticEngine.dualPulse(context)
            while (elapsedSec < totalSec) {
                delay(1000)
                elapsedSec++
                when {
                    elapsedSec >= totalSec -> Unit
                    elapsedSec == totalSec / 2 -> HapticEngine.midpointAccent(context)
                    elapsedSec % 60 == 0 -> HapticEngine.dualPulse(context)
                }
            }
            HapticEngine.resolvingWave(context)
            running = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Session length",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIACharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 3, 5, 10).forEach { m ->
                val selected = durationMin == m
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable(enabled = !running) { durationMin = m },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite,
                    border = BorderStroke(
                        if (selected) 2.dp else 1.dp,
                        if (selected) OIASage else OIATaupe.copy(alpha = 0.4f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$m min",
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = OIACharcoal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val remaining = (totalSec - elapsedSec).coerceAtLeast(0)
        Text(
            text = if (running) "${formatMmSs(remaining)} remaining" else "Ready when you are",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (running) OIASage else OIAStone
        )
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { if (totalSec > 0) elapsedSec.toFloat() / totalSec else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = OIASage,
            trackColor = OIATaupe.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OIAPrimaryButton(
            text = if (running) "Stop" else "Start Pocket Anchor",
            backgroundColor = if (running) OIATaupe else OIASage,
            onClick = {
                if (running) {
                    running = false
                    HapticEngine.cancel(context)
                } else {
                    running = true
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A steady pulse every minute — rest the phone in your pocket and let the rhythm carry you. Screen-off background mode is coming soon.",
            fontSize = 12.sp,
            color = OIAStone
        )
    }
}

private fun formatMmSs(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}
