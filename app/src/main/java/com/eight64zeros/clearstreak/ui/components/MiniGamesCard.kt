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
import com.eight64zeros.clearstreak.game.BlockDropBoard
import com.eight64zeros.clearstreak.game.Game2048Board
import com.eight64zeros.clearstreak.game.PatternEchoBoard
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

private enum class MiniGame { TILE_MERGE, PATTERN_ECHO, BLOCK_DROP }

/**
 * ClearStreak wrapper around the portable game boards. Owns the recovery-specific
 * concerns — a game picker, the metric readout, and the ~3-minute time-box
 * awareness banner (a question/offer, never a command). The game cores know
 * nothing about ClearStreak.
 */
@Composable
fun MiniGamesCard(modifier: Modifier = Modifier) {
    var game by remember { mutableStateOf(MiniGame.TILE_MERGE) }
    var score by remember { mutableStateOf(0) }
    var round by remember { mutableStateOf(1) }
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
            Text(
                text = when (game) {
                    MiniGame.TILE_MERGE -> "🧩 Tile Merge"
                    MiniGame.PATTERN_ECHO -> "🔢 Pattern Echo"
                    MiniGame.BLOCK_DROP -> "🟦 Block Drop"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OIACharcoal
            )
            Text(
                text = when (game) {
                    MiniGame.PATTERN_ECHO -> "Round $round"
                    else -> "Score $score"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIASage
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameChip(
                label = "Tiles",
                selected = game == MiniGame.TILE_MERGE,
                modifier = Modifier.weight(1f)
            ) {
                game = MiniGame.TILE_MERGE
                score = 0
            }
            GameChip(
                label = "Echo",
                selected = game == MiniGame.PATTERN_ECHO,
                modifier = Modifier.weight(1f)
            ) {
                game = MiniGame.PATTERN_ECHO
                round = 1
            }
            GameChip(
                label = "Blocks",
                selected = game == MiniGame.BLOCK_DROP,
                modifier = Modifier.weight(1f)
            ) {
                game = MiniGame.BLOCK_DROP
                score = 0
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when (game) {
                MiniGame.TILE_MERGE -> "Slide to merge matching tiles."
                MiniGame.PATTERN_ECHO -> "Watch the pattern, then tap it back. It grows each round."
                MiniGame.BLOCK_DROP -> "Rotate and drop the blocks to clear full rows."
            },
            fontSize = 13.sp,
            color = OIAStone
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (game) {
            MiniGame.TILE_MERGE -> Game2048Board(onScoreChanged = { score = it })
            MiniGame.PATTERN_ECHO -> PatternEchoBoard(onRoundChanged = { round = it })
            MiniGame.BLOCK_DROP -> BlockDropBoard(onScoreChanged = { score = it })
        }

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

@Composable
private fun GameChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) OIASage else OIATaupe.copy(alpha = 0.4f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = OIACharcoal
            )
        }
    }
}
