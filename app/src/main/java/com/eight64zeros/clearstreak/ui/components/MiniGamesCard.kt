package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.game.Game2048Board
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

/**
 * ClearStreak wrapper around the portable [Game2048Board]. Owns the recovery-
 * specific concerns — score readout and the ~3-minute time-box awareness banner
 * (a question/offer, never a command). The game core knows nothing about this.
 */
@Composable
fun MiniGamesCard(modifier: Modifier = Modifier) {
    var score by remember { mutableStateOf(0) }
    var showBanner by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(180_000L) // ~3 min — the craving-wave window
        showBanner = true
    }

    OIACard(
        modifier = modifier,
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🧩 Tile Merge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
            Text(text = "Score $score", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Slide to merge matching tiles. A few focused minutes to let the craving wave crest and pass.",
            fontSize = 13.sp,
            color = OIAStone
        )
        Spacer(modifier = Modifier.height(16.dp))

        Game2048Board(onScoreChanged = { score = it })

        if (showBanner) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OIASage.copy(alpha = 0.12f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Craving waves usually peak and pass within a few minutes.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIACharcoal
                    )
                    Text(
                        text = "You've given it space. Keep playing, or set the phone down when you're ready.",
                        fontSize = 12.sp,
                        color = OIAStone
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Keep playing",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIASage,
                        modifier = Modifier.clickable { showBanner = false }
                    )
                }
            }
        }
    }
}
