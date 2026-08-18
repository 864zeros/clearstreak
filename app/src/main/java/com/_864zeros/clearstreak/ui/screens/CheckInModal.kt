package com._864zeros.clearstreak.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.model.CheckIn
import com._864zeros.clearstreak.model.CopingCard
import com._864zeros.clearstreak.model.HaltTrigger
import com._864zeros.clearstreak.model.Journey
import com._864zeros.clearstreak.model.UrgeLevel
import com._864zeros.clearstreak.ui.components.CopingCardView
import com._864zeros.clearstreak.ui.components.GroundingTimer
import com._864zeros.clearstreak.ui.components.HaltTriggerRow
import com._864zeros.clearstreak.ui.components.OIACard
import com._864zeros.clearstreak.ui.components.OIAPrimaryButton
import com._864zeros.clearstreak.ui.components.OIASecondaryButton
import com._864zeros.clearstreak.ui.components.OIATextField
import com._864zeros.clearstreak.ui.components.UrgePulseGrid
import com._864zeros.clearstreak.ui.theme.OIACharcoal
import com._864zeros.clearstreak.ui.theme.OIACoral
import com._864zeros.clearstreak.ui.theme.OIACream
import com._864zeros.clearstreak.ui.theme.OIAError
import com._864zeros.clearstreak.ui.theme.OIAMustard
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIAStone
import com._864zeros.clearstreak.ui.theme.OIATaupe
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun CheckInModal(
    journey: Journey,
    onSaveCheckIn: (CheckIn) -> Unit,
    onTriggerCrisis: () -> Unit,
    onDismiss: () -> Unit,
    onRequestCopingCard: (HaltTrigger?, UrgeLevel) -> CopingCard?
) {
    var selectedUrge by remember { mutableStateOf<UrgeLevel?>(null) }
    var selectedHalt by remember { mutableStateOf<HaltTrigger?>(null) }
    var moodScore by remember { mutableStateOf<Int?>(null) }
    var noteText by remember { mutableStateOf("") }
    var isSlip by remember { mutableStateOf(false) }
    var showGroundingTimer by remember { mutableStateOf(false) }
    var activeCopingCard by remember { mutableStateOf<CopingCard?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OIACream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Check In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIACharcoal
                    )
                    Text(
                        text = journey.title,
                        fontSize = 14.sp,
                        color = OIAStone
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OIACharcoal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 1: Urge Grid
            Text(
                text = "How are you feeling right now?",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )

            Spacer(modifier = Modifier.height(12.dp))

            UrgePulseGrid(
                selectedUrge = selectedUrge,
                onSelectUrge = { urge ->
                    selectedUrge = urge
                    if (urge == UrgeLevel.CRITICAL) {
                        onTriggerCrisis()
                    } else if (urge == UrgeLevel.WHITE_KNUCKLING) {
                        activeCopingCard = onRequestCopingCard(selectedHalt, urge)
                    }
                }
            )

            // Step 2: HALT Context (for Passing & White-Knuckling)
            AnimatedVisibility(
                visible = selectedUrge == UrgeLevel.PASSING || selectedUrge == UrgeLevel.WHITE_KNUCKLING
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "What is driving this? (HALT)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HaltTriggerRow(
                        selectedTrigger = selectedHalt,
                        onSelectTrigger = { trigger ->
                            selectedHalt = trigger
                            if (selectedUrge == UrgeLevel.WHITE_KNUCKLING) {
                                activeCopingCard = onRequestCopingCard(trigger, UrgeLevel.WHITE_KNUCKLING)
                            }
                        }
                    )
                }
            }

            // Step 3: Coping Strategy & Grounding (for White-Knuckling)
            AnimatedVisibility(visible = selectedUrge == UrgeLevel.WHITE_KNUCKLING) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    activeCopingCard?.let { card ->
                        CopingCardView(card = card)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!showGroundingTimer) {
                        OIASecondaryButton(
                            text = "🫁 Launch 60-Sec Grounding Timer",
                            onClick = { showGroundingTimer = true }
                        )
                    } else {
                        OIACard(
                            backgroundColor = com._864zeros.clearstreak.ui.theme.OIADarkCard,
                            cornerRadius = 16.dp,
                            padding = 16.dp
                        ) {
                            GroundingTimer(onFinished = { showGroundingTimer = false })
                        }
                    }
                }
            }

            // Step 4: Mood Rating (1 to 5)
            AnimatedVisibility(visible = selectedUrge == UrgeLevel.CLEAR) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "Mood rating",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val moodEmojis = listOf("😞", "😕", "😐", "🙂", "😄")
                        moodEmojis.forEachIndexed { index, emoji ->
                            val score = index + 1
                            val isSelected = moodScore == score
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clickable { moodScore = score },
                                shape = CircleShape,
                                color = if (isSelected) OIASage.copy(alpha = 0.25f) else OIAWarmWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) OIASage else OIATaupe.copy(alpha = 0.3f)
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Step 5: Optional Encrypted Note
            AnimatedVisibility(visible = selectedUrge != null && selectedUrge != UrgeLevel.CRITICAL) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "Encrypted reflection (optional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OIATextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = "What is on your mind? Only your biometrics can decrypt this.",
                        singleLine = false,
                        maxLines = 4,
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Non-Shaming Slip Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSlip = !isSlip }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSlip,
                            onCheckedChange = { isSlip = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OIACoral,
                                uncheckedColor = OIATaupe
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Log a slip (reset active streak, keep history)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = OIACharcoal
                            )
                            Text(
                                text = "Slips are data points, not failures. All cumulative days remain intact.",
                                fontSize = 12.sp,
                                color = OIAStone
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OIAPrimaryButton(
                        text = if (isSlip) "Log Slip & Reset Streak" else "Save Check-In",
                        onClick = {
                            val checkIn = CheckIn(
                                journeyId = journey.id,
                                moodScore = moodScore,
                                urgeLevel = selectedUrge ?: UrgeLevel.CLEAR,
                                haltTrigger = selectedHalt,
                                noteEncrypted = noteText.ifBlank { null },
                                isSlip = isSlip,
                                isCrisisIntercept = false,
                                copingCardId = activeCopingCard?.id
                            )
                            onSaveCheckIn(checkIn)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
