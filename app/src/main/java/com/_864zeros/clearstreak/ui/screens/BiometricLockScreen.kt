package com._864zeros.clearstreak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.ui.components.OIAPrimaryButton
import com._864zeros.clearstreak.ui.theme.OIACream
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIAStone
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun BiometricLockScreen(
    onAuthenticateClicked: () -> Unit,
    errorMessage: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OIACream)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = OIASage.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = OIASage,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ClearStreak",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OIASage
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your recovery is yours alone.\nEncrypted with hardware-bound keys.",
                fontSize = 15.sp,
                color = OIAStone,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = com._864zeros.clearstreak.ui.theme.OIAError,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            OIAPrimaryButton(
                text = "Unlock with Biometrics",
                onClick = onAuthenticateClicked
            )
        }
    }
}
