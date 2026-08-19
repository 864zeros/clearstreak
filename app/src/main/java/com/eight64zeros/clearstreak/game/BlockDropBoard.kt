package com.eight64zeros.clearstreak.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Monochrome slate palette (no per-piece color mapping) — trade-dress safe.
private val WellBg = Color(0xFFCFC8BC)
private val EmptyCell = Color(0xFFDAD4C9)
private val LockedCell = Color(0xFF6B6459)
private val ActiveCell = Color(0xFF8A8275)
private val ControlBg = Color(0xFFE8E3DA)
private val ControlText = Color(0xFF2D2D2D)
private val OverlayText = Color(0xFFFDFCFA)

/**
 * Self-contained falling-block board. Depends only on Compose and its own
 * engine. Gravity runs on a coroutine timer; controls are on-screen buttons.
 */
@Composable
fun BlockDropBoard(
    modifier: Modifier = Modifier,
    onScoreChanged: (Int) -> Unit = {}
) {
    var state by remember { mutableStateOf(BlockDropState.new()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(650)
            if (!state.over) {
                state = state.step()
                onScoreChanged(state.score)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(340.dp)
                .aspectRatio(BD_WIDTH.toFloat() / BD_HEIGHT)
                .clip(RoundedCornerShape(10.dp))
                .background(WellBg)
                .padding(3.dp)
        ) {
            val active = remember(state) { state.activeCells().toHashSet() }
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until BD_HEIGHT) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (c in 0 until BD_WIDTH) {
                            val color = when {
                                state.grid[r][c] != 0 -> LockedCell
                                Cell(r, c) in active -> ActiveCell
                                else -> EmptyCell
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(1.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }

            if (state.over) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xCC1A1A1A))
                        .clickable { state = BlockDropState.new() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Stack topped out", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OverlayText)
                        Text(text = "Tap to play again", fontSize = 13.sp, color = OverlayText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlButton("◀", Modifier.weight(1f)) { state = state.moveLeft() }
            ControlButton("⟳", Modifier.weight(1f)) { state = state.rotate() }
            ControlButton("▶", Modifier.weight(1f)) { state = state.moveRight() }
            ControlButton("⤓", Modifier.weight(1.4f)) {
                state = state.hardDrop()
                onScoreChanged(state.score)
            }
        }
    }
}

@Composable
private fun ControlButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ControlBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ControlText)
    }
}
