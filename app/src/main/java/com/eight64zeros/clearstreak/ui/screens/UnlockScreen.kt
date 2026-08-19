package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.billing.PremiumState
import com.eight64zeros.clearstreak.billing.PremiumStatus
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

/**
 * One-time unlock paywall. Deliberately un-pushy: crisis and core recovery tools are always free,
 * this only unlocks the supplemental library/features, once, forever — no subscription.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockScreen(
    state: PremiumState,
    onUnlockClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Unlock ClearStreak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OIACharcoal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OIACharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OIACream)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "✨", fontSize = 40.sp)

            if (state.isUnlocked) {
                Text(
                    text = "You're all set",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Text(
                    text = "ClearStreak is unlocked on this phone — everything, forever. Thank you for supporting privacy-first recovery.",
                    fontSize = 15.sp,
                    color = OIAStone,
                    lineHeight = 22.sp
                )
            } else {
                Text(
                    text = "Everything, forever",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Text(
                    text = "One payment unlocks the full app on this phone. No subscription, no account, no tracking — the same promise as everything else here.",
                    fontSize = 15.sp,
                    color = OIAStone,
                    lineHeight = 22.sp
                )

                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 18.dp
                ) {
                    BenefitRow("📚", "The full library", "Every recovery passage and scripture reflection, browsable by theme.")
                    BenefitRow("🌱", "Unlimited journeys", "Track as many recoveries as you need, side by side.")
                    BenefitRow("📅", "Daily passage on Home", "A fresh recovery passage waiting each morning.")
                    BenefitRow("♾️", "All future updates", "Everything we add later is included — you never pay again.")
                }

                // The ethos / anti-subscription reassurance.
                OIACard(
                    backgroundColor = OIASage.copy(alpha = 0.12f),
                    borderColor = OIASage.copy(alpha = 0.3f),
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Text(
                        text = "Crisis tools, streak tracking, and your encrypted journal are always free — in a hard moment, nothing is ever behind a paywall.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = OIACharcoal,
                        lineHeight = 20.sp
                    )
                }
            }

            state.message?.let { msg ->
                Text(
                    text = msg,
                    fontSize = 13.sp,
                    color = OIACoral,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!state.isUnlocked) {
                val unavailable = state.status == PremiumStatus.UNAVAILABLE
                val pending = state.status == PremiumStatus.PURCHASE_PENDING
                OIAPrimaryButton(
                    text = when {
                        pending -> "Waiting for payment…"
                        unavailable -> "Store unavailable"
                        else -> "Unlock — ${state.priceText}"
                    },
                    onClick = onUnlockClicked,
                    enabled = !unavailable && !pending
                )
                Text(
                    text = "One-time purchase • billed once through Google Play",
                    fontSize = 12.sp,
                    color = OIAStone,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                TextButton(
                    onClick = onRestoreClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already bought it? Restore purchase",
                        fontSize = 14.sp,
                        color = OIASage,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = OIACharcoal)
            Text(text = description, fontSize = 13.sp, color = OIAStone, lineHeight = 18.sp)
        }
    }
}
