package com.eight64zeros.clearstreak.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoralLight
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIASageLight
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

enum class BreathPhase(val instruction: String, val durationSec: Int) {
    INHALE("Inhale (Nose)", 4),
    HOLD("Hold Breath", 7),
    EXHALE("Exhale (Mouth)", 8)
}

/**
 * 4-7-8 breathing guide for the Crisis Intercept. Auto-starts (crisis context),
 * with Pause/Resume and Reset controls. Phase-indexed so Resume continues from
 * the current phase.
 */
@Composable
fun BreathingCircle(
    modifier: Modifier = Modifier,
    onLight: Boolean = false
) {
    val context = LocalContext.current
    val textColor = if (onLight) OIACharcoal else OIAWarmWhite
    var running by remember { mutableStateOf(true) }
    var phaseIndex by remember { mutableIntStateOf(0) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var secondsRemaining by remember { mutableIntStateOf(4) }
    var targetScale by remember { mutableStateOf(0.4f) }

    fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(80)
            }
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        val phases = BreathPhase.entries
        while (true) {
            val p = phases[phaseIndex % phases.size]
            currentPhase = p
            targetScale = when (p) {
                BreathPhase.INHALE -> 1.0f
                BreathPhase.HOLD -> targetScale
                BreathPhase.EXHALE -> 0.4f
            }
            triggerHaptic()
            for (sec in p.durationSec downTo 1) {
                secondsRemaining = sec
                delay(1000)
            }
            phaseIndex = (phaseIndex + 1) % phases.size
        }
    }

    val animDuration = when (currentPhase) {
        BreathPhase.INHALE -> 4000
        BreathPhase.HOLD -> 0
        BreathPhase.EXHALE -> 8000
    }
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = animDuration, easing = LinearEasing),
        label = "breathCircleScale"
    )
    val circleColor = when (currentPhase) {
        BreathPhase.INHALE -> OIASageLight
        BreathPhase.HOLD -> OIACoralLight
        BreathPhase.EXHALE -> OIASage
    }

    val view = LocalView.current
    DisposableEffect(running) {
        view.keepScreenOn = running
        onDispose { view.keepScreenOn = false }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(circleColor.copy(alpha = 0.6f))
            )
            Text(
                text = "$secondsRemaining",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (running) currentPhase.instruction else "Paused",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OIAPrimaryButton(
                text = if (running) "Pause" else if (phaseIndex == 0) "Start" else "Resume",
                onClick = { running = !running },
                modifier = Modifier.weight(1f)
            )
            OIASecondaryButton(
                text = "Reset",
                onClick = {
                    running = false
                    phaseIndex = 0
                    currentPhase = BreathPhase.INHALE
                    secondsRemaining = 4
                    targetScale = 0.4f
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
