package com.eight64zeros.clearstreak.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.delay

@Composable
fun GroundingTimer(
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var secondsLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        onFinished()
    }

    val progress by animateFloatAsState(
        targetValue = secondsLeft / 60f,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "groundingProgress"
    )

    val prompt = when {
        secondsLeft > 48 -> "👀 Name 5 things you can see right now."
        secondsLeft > 36 -> "✋ Name 4 things you can physically feel."
        secondsLeft > 24 -> "👂 Name 3 sounds you can hear."
        secondsLeft > 12 -> "👃 Name 2 things you can smell around you."
        else -> "❤️ Name 1 thing you are grateful for today."
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1.0f },
                modifier = Modifier.size(140.dp),
                color = OIATaupe.copy(alpha = 0.25f),
                strokeWidth = 8.dp
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(140.dp),
                color = OIASage,
                strokeWidth = 8.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${secondsLeft}s",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIAWarmWhite
                )
                Text(
                    text = "Grounding",
                    fontSize = 12.sp,
                    color = OIATaupe
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = prompt,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = OIAWarmWhite,
            textAlign = TextAlign.Center
        )
    }
}
