package com._864zeros.clearstreak.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.ui.theme.OIACoralLight
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIASageLight
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

enum class BreathPhase(val instruction: String, val durationSec: Int) {
    INHALE("Inhale (Nose)", 4),
    HOLD("Hold Breath", 7),
    EXHALE("Exhale (Mouth)", 8)
}

@Composable
fun BreathingCircle(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                v?.vibrate(80)
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(Unit) {
        while (true) {
            // Phase 1: Inhale (4s)
            currentPhase = BreathPhase.INHALE
            targetScale = 1.0f
            triggerHaptic()
            for (sec in 4 downTo 1) {
                secondsRemaining = sec
                delay(1000)
            }

            // Phase 2: Hold (7s)
            currentPhase = BreathPhase.HOLD
            triggerHaptic()
            for (sec in 7 downTo 1) {
                secondsRemaining = sec
                delay(1000)
            }

            // Phase 3: Exhale (8s)
            currentPhase = BreathPhase.EXHALE
            targetScale = 0.4f
            triggerHaptic()
            for (sec in 8 downTo 1) {
                secondsRemaining = sec
                delay(1000)
            }
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer subtle boundary
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            // Dynamic breathing circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(circleColor.copy(alpha = 0.6f))
            )

            // Inner countdown
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$secondsRemaining",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIAWarmWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentPhase.instruction,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIAWarmWhite
        )
    }
}
