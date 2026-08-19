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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Self-contained pad palette (dim / lit), no external dependencies.
private val PadDim = listOf(
    Color(0xFFB57A70), // coral
    Color(0xFF6E856B), // sage
    Color(0xFF9E834F), // gold
    Color(0xFF5E7284)  // dusty blue
)
private val PadLit = listOf(
    Color(0xFFE8A598),
    Color(0xFF8BA888),
    Color(0xFFC9A86C),
    Color(0xFF7A8FA3)
)
private val StatusText = Color(0xFF2D2D2D)
private val OverlayText = Color(0xFFFDFCFA)

/**
 * Self-contained sequence-memory board. Depends only on Compose and its own
 * engine. Reports the current round via [onRoundChanged].
 */
@Composable
fun PatternEchoBoard(
    modifier: Modifier = Modifier,
    onRoundChanged: (Int) -> Unit = {}
) {
    var state by remember { mutableStateOf(PatternEchoState.new()) }
    var showing by remember { mutableStateOf(true) }
    var flashing by remember { mutableStateOf<Int?>(null) }
    var tappedFlash by remember { mutableStateOf<Int?>(null) }
    var restartToken by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Replay the sequence whenever the round advances or the game restarts.
    LaunchedEffect(state.round, restartToken) {
        if (state.over) return@LaunchedEffect
        showing = true
        flashing = null
        delay(500)
        for (pad in state.sequence) {
            flashing = pad
            delay(450)
            flashing = null
            delay(220)
        }
        showing = false
    }

    LaunchedEffect(state.round) { onRoundChanged(state.round) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                state.over -> "Sequence broken"
                showing -> "Watch the pattern"
                else -> "Your turn — repeat it"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = StatusText
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (rowIdx in 0 until 2) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (colIdx in 0 until 2) {
                            val i = rowIdx * 2 + colIdx
                            val lit = flashing == i || tappedFlash == i
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (lit) PadLit[i] else PadDim[i])
                                    .clickable(enabled = !showing && !state.over) {
                                        tappedFlash = i
                                        scope.launch {
                                            delay(180)
                                            tappedFlash = null
                                        }
                                        state = state.tap(i)
                                    }
                            )
                        }
                    }
                }
            }

            if (state.over) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xAA1A1A1A))
                        .clickable {
                            state = PatternEchoState.new()
                            restartToken++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap to try again",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OverlayText
                    )
                }
            }
        }
    }
}
