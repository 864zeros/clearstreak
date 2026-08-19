package com.eight64zeros.clearstreak.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.HapticEngine
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIASageLight
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import kotlinx.coroutines.delay

private enum class BoxBreathPhase(val label: String) {
    INHALE("Breathe in"),
    HOLD_FULL("Hold"),
    EXHALE("Breathe out"),
    HOLD_EMPTY("Hold")
}

/**
 * 4-4-4-4 tactile box breather (blueprint §2). Each phase lasts 4s and drives a
 * distinct haptic cue from [HapticEngine]; the ring scales with the breath.
 */
@Composable
fun BoxBreather(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var phase by remember { mutableStateOf(BoxBreathPhase.INHALE) }
    var secondsLeft by remember { mutableStateOf(4) }

    val targetScale = when (phase) {
        BoxBreathPhase.INHALE, BoxBreathPhase.HOLD_FULL -> 1f
        BoxBreathPhase.EXHALE, BoxBreathPhase.HOLD_EMPTY -> 0.55f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
        label = "boxBreathScale"
    )

    LaunchedEffect(Unit) {
        val order = listOf(
            BoxBreathPhase.INHALE,
            BoxBreathPhase.HOLD_FULL,
            BoxBreathPhase.EXHALE,
            BoxBreathPhase.HOLD_EMPTY
        )
        var i = 0
        while (true) {
            val p = order[i % order.size]
            phase = p
            when (p) {
                BoxBreathPhase.INHALE -> HapticEngine.inhale(context)
                BoxBreathPhase.HOLD_FULL -> HapticEngine.holdTicks(context)
                BoxBreathPhase.EXHALE -> HapticEngine.exhale(context)
                BoxBreathPhase.HOLD_EMPTY -> HapticEngine.restPulse(context)
            }
            for (s in 4 downTo 1) {
                secondsLeft = s
                delay(1000)
            }
            i++
        }
    }

    // Keep the screen awake while the breather is on-screen (design assumes the
    // phone is on). No permission required.
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(OIASageLight.copy(alpha = 0.30f))
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(OIASage.copy(alpha = 0.45f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = phase.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OIACharcoal
                )
                Text(
                    text = "$secondsLeft",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIASage
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Follow the pulse — in, hold, out, hold.",
            fontSize = 13.sp,
            color = OIAStone
        )
    }
}
