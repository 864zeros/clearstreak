package com.eight64zeros.clearstreak.ui.screens

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
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIAError
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone

/**
 * SETUP = first run (choose/confirm a lock); UNLOCK = returning; NEEDS_DEVICE_LOCK = the phone has
 * no screen lock at all, so we send the user to set one before an encrypted vault can exist.
 */
enum class LockMode { SETUP, UNLOCK, NEEDS_DEVICE_LOCK }

@Composable
fun BiometricLockScreen(
    mode: LockMode,
    onPrimaryAction: () -> Unit,
    errorMessage: String? = null
) {
    val title = "ClearStreak"
    val body = when (mode) {
        LockMode.SETUP -> "Lock ClearStreak with your fingerprint, face, or device PIN — whichever your phone uses. Only you can unlock it, and your recovery never leaves this device."
        LockMode.UNLOCK -> "Unlock with your fingerprint, face, or device PIN."
        LockMode.NEEDS_DEVICE_LOCK -> "ClearStreak encrypts your recovery with your phone's own security — so it needs a screen lock (PIN, pattern, password, or biometric). Set one up to continue."
    }
    val button = when (mode) {
        LockMode.SETUP -> "Set up your lock & continue"
        LockMode.UNLOCK -> "Unlock"
        LockMode.NEEDS_DEVICE_LOCK -> "Open security settings"
    }

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
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OIASage
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = body,
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
                    color = OIAError,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            OIAPrimaryButton(text = button, onClick = onPrimaryAction)
        }
    }
}
