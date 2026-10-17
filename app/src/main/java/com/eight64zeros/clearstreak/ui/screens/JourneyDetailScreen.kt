package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.eight64zeros.clearstreak.data.StreakCalculator
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.ui.components.CalendarHeatmap
import com.eight64zeros.clearstreak.ui.components.MilestoneCoin
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.OIASecondaryButton
import com.eight64zeros.clearstreak.ui.components.OIATextField
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIARadiusPill
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JourneyDetailScreen(
    journey: Journey,
    checkIns: List<CheckIn>,
    onCheckInClicked: () -> Unit,
    onArchiveClicked: () -> Unit,
    onBack: () -> Unit,
    onTogglePrimaryFocus: (Boolean) -> Unit = {},
    onUpdateHabitPlan: (String?, String?) -> Unit = { _, _ -> }
) {
    val stats = StreakCalculator.calculateStats(journey, checkIns)
    val journeyCheckIns = checkIns.filter { it.journeyId == journey.id }
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val startDateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(journey.startTimestamp * 1000))
    var routineAnchorDraft by remember(journey.id, journey.routineAnchor) { mutableStateOf(journey.routineAnchor.orEmpty()) }
    var floorRepDraft by remember(journey.id, journey.floorRep) { mutableStateOf(journey.floorRep.orEmpty()) }
    var isEditingPlan by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = journey.title,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Stats Card
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 20.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Started: $startDateStr",
                            fontSize = 13.sp,
                            color = OIAStone
                        )
                        if (journey.isPrimaryFocus) {
                            Surface(
                                shape = OIARadiusPill,
                                color = OIASage.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🎯 Primary Focus",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OIASage,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${stats.currentStreakDays} Days Clear",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIASage
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Longest Streak", fontSize = 12.sp, color = OIAStone)
                            Text(
                                text = "${stats.longestStreakDays} days",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OIACharcoal
                            )
                        }
                        Column {
                            Text(text = "Cumulative Days", fontSize = 12.sp, color = OIAStone)
                            Text(
                                text = "${stats.cumulativeDaysClear} days",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OIACharcoal
                            )
                        }
                        if (journey.dailyCostSavings > 0) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Money Saved", fontSize = 12.sp, color = OIAStone)
                                Text(
                                    text = "$${String.format("%.2f", stats.totalMoneySaved)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OIASage
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OIAPrimaryButton(
                        text = "Check In Now",
                        onClick = onCheckInClicked
                    )
                }
            }

            // 4-Pillar Habit Engine Plan (Primary Focus + Routine Anchor + Floor Rep)
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎯 Primary Focus Pin",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OIACharcoal
                            )
                            Text(
                                text = "Pin to top of Home as your single undivided priority (Franklin 1791).",
                                fontSize = 12.sp,
                                color = OIAStone
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = journey.isPrimaryFocus,
                            onCheckedChange = onTogglePrimaryFocus
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditingPlan) {
                        Text(
                            text = "⚓ When–Then Routine Anchor: ${journey.routineAnchor?.takeIf { it.isNotBlank() } ?: "Not set"}",
                            fontSize = 13.sp,
                            color = OIACharcoal,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🛡️ Floor Rep (Minimum Viable Day): ${journey.floorRep?.takeIf { it.isNotBlank() } ?: "Not set"}",
                            fontSize = 13.sp,
                            color = OIASage,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OIASecondaryButton(
                            text = "Edit Routine Anchor & Floor Rep",
                            onClick = { isEditingPlan = true }
                        )
                    } else {
                        Text(
                            text = "⚓ When–Then Routine Anchor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OIACharcoal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OIATextField(
                            value = routineAnchorDraft,
                            onValueChange = { routineAnchorDraft = it },
                            placeholder = "After / When I [daily anchor], I will [action]"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🛡️ Floor Rep (Minimum Viable Action)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OIACharcoal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OIATextField(
                            value = floorRepDraft,
                            onValueChange = { floorRepDraft = it },
                            placeholder = "e.g., 5 pushups, 1 page read, or 60-sec breath"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OIAPrimaryButton(
                            text = "Save Habit Plan",
                            onClick = {
                                onUpdateHabitPlan(
                                    routineAnchorDraft.trim().ifBlank { null },
                                    floorRepDraft.trim().ifBlank { null }
                                )
                                isEditingPlan = false
                            }
                        )
                    }
                }
            }

            // Non-Shaming Record & Slip Framing (Brick 2)
            item {
                OIACard(
                    backgroundColor = OIASage.copy(alpha = 0.12f),
                    borderColor = OIASage.copy(alpha = 0.4f),
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Text(
                        text = "Your progress is permanent",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIASage
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = StreakCalculator.slipFraming(stats),
                        fontSize = 14.sp,
                        color = OIACharcoal,
                        lineHeight = 20.sp
                    )
                }
            }

            // Achievements / Milestone Badges (Brick 2)
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Text(
                        text = "Milestone Coins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (stats.achievedMilestones.isEmpty())
                            "Your first coin lands at Day 1. Every coin you earn is permanent — yours to keep."
                        else
                            "${stats.achievedMilestones.size} earned • next: ${stats.nextMilestoneName}",
                        fontSize = 13.sp,
                        color = OIAStone
                    )
                    if (stats.achievedMilestones.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            stats.achievedMilestones.forEach { m ->
                                MilestoneCoin(milestone = m)
                            }
                        }
                    }
                }
            }

            // Progress Calendar / Heatmap (Brick 3)
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Text(
                        text = "Progress Calendar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CalendarHeatmap(checkIns = journeyCheckIns)
                }
            }

            // History Timeline Section Header
            item {
                Text(
                    text = "History & Check-Ins",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
            }

            if (journeyCheckIns.isEmpty()) {
                item {
                    Text(
                        text = "No check-ins recorded yet. Tap Check In Now to record your first moment.",
                        fontSize = 14.sp,
                        color = OIAStone
                    )
                }
            } else {
                items(journeyCheckIns.reversed()) { checkIn ->
                    OIACard(
                        backgroundColor = OIAWarmWhite,
                        cornerRadius = 14.dp,
                        padding = 14.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = OIARadiusPill,
                                color = if (checkIn.isSlip) OIACoral.copy(alpha = 0.2f) else OIASage.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (checkIn.isSlip) "Slip Logged • New Start" else "${checkIn.urgeLevel.emoji} ${checkIn.urgeLevel.displayName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (checkIn.isSlip) OIACoral else OIASage,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = dateFormat.format(Date(checkIn.timestamp * 1000)),
                                fontSize = 12.sp,
                                color = OIATaupe
                            )
                        }

                        checkIn.haltTrigger?.let { halt ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "HALT: ${halt.emoji} ${halt.displayName}",
                                fontSize = 13.sp,
                                color = OIAStone
                            )
                        }

                        if (!checkIn.noteEncrypted.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = checkIn.noteEncrypted,
                                fontSize = 14.sp,
                                color = OIACharcoal
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                OIASecondaryButton(
                    text = "Archive Journey",
                    onClick = onArchiveClicked,
                    contentColor = OIAStone,
                    borderColor = OIATaupe
                )
            }
        }
    }
}
