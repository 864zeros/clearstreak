package com.eight64zeros.clearstreak.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

// Self-contained warm-neutral / slate palette — no external or proprietary
// color mapping, keeping the game module free of dependencies.
private val BoardBg = Color(0xFFBDB5A8)
private val EmptyCell = Color(0xFFCFC8BC)
private val TileDark = Color(0xFF2D2D2D)
private val TileLight = Color(0xFFFDFCFA)

private fun tileColor(value: Int): Color = when (value) {
    0 -> EmptyCell
    2 -> Color(0xFFF0EAE0)
    4 -> Color(0xFFEADFC8)
    8 -> Color(0xFFE8A598)
    16 -> Color(0xFFE0917F)
    32 -> Color(0xFFD97D66)
    64 -> Color(0xFFCF6A50)
    128 -> Color(0xFFC9A86C)
    256 -> Color(0xFFBF9A52)
    512 -> Color(0xFF8BA888)
    1024 -> Color(0xFF6F9A6B)
    else -> Color(0xFF4F7A4C) // 2048+
}

private fun tileTextColor(value: Int): Color = if (value <= 4) TileDark else TileLight

/**
 * Self-contained 2048 board composable. Depends only on Compose and its own
 * engine — no ClearStreak imports. Reports score changes via [onScoreChanged].
 */
@Composable
fun Game2048Board(
    modifier: Modifier = Modifier,
    onScoreChanged: (Int) -> Unit = {}
) {
    var state by remember { mutableStateOf(Game2048State.new()) }
    var dragTotal by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(BoardBg)
                .padding(6.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragTotal = Offset.Zero },
                        onDrag = { change, amount ->
                            change.consume()
                            dragTotal += amount
                        },
                        onDragEnd = {
                            if (!state.over) {
                                val dx = dragTotal.x
                                val dy = dragTotal.y
                                val threshold = 40f
                                val dir = if (abs(dx) > abs(dy)) {
                                    if (abs(dx) > threshold) (if (dx > 0) SwipeDir.RIGHT else SwipeDir.LEFT) else null
                                } else {
                                    if (abs(dy) > threshold) (if (dy > 0) SwipeDir.DOWN else SwipeDir.UP) else null
                                }
                                if (dir != null) {
                                    state = state.moved(dir)
                                    onScoreChanged(state.score)
                                }
                            }
                        }
                    )
                },
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (r in 0 until BOARD_SIZE) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (c in 0 until BOARD_SIZE) {
                        val value = state.grid[r][c]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(tileColor(value)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (value != 0) {
                                Text(
                                    text = "$value",
                                    fontSize = if (value >= 1024) 16.sp else 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tileTextColor(value)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.over) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCC1A1A1A))
                    .clickable {
                        state = Game2048State.new()
                        onScoreChanged(0)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Grid full", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TileLight)
                    Text(text = "Tap to play again", fontSize = 14.sp, color = TileLight)
                }
            }
        }
    }
}
