package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.StreakCalculator
import com.eight64zeros.clearstreak.model.BookPassage
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.DailyVerse
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.OIASecondaryButton
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIADustyBlue
import com.eight64zeros.clearstreak.ui.theme.OIAError
import com.eight64zeros.clearstreak.ui.theme.OIAMustard
import com.eight64zeros.clearstreak.ui.theme.OIARadiusPill
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIASageDark
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun DashboardScreen(
    journeys: List<Journey>,
    checkIns: List<CheckIn>,
    dailyVerse: DailyVerse?,
    dailyPassage: BookPassage?,
    showFaith: Boolean,
    onCheckInClicked: (Journey) -> Unit,
    onJourneySelected: (Journey) -> Unit,
    onAddJourneyClicked: () -> Unit,
    onCrisisTriggered: () -> Unit,
    onNavigateJournal: () -> Unit,
    onNavigateReset: () -> Unit,
    onNavigateHeritage: () -> Unit,
    onNavigateSettings: () -> Unit,
    onLockApp: () -> Unit
) {
    Scaffold(
        containerColor = OIACream,
        floatingActionButton = {
            if (journeys.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onCheckInClicked(journeys.first()) },
                    containerColor = OIASage,
                    contentColor = OIAWarmWhite,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check In",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = OIAWarmWhite,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on Home */ },
                    icon = { Icon(Icons.Default.FlashOn, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OIASage,
                        selectedTextColor = OIASage,
                        indicatorColor = OIASage.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateJournal,
                    icon = { Icon(Icons.Outlined.Book, contentDescription = "Journal") },
                    label = { Text("Journal", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = OIATaupe,
                        unselectedTextColor = OIAStone
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateReset,
                    icon = { Icon(Icons.Outlined.Spa, contentDescription = "Reset") },
                    label = { Text("Reset", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = OIATaupe,
                        unselectedTextColor = OIAStone
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateHeritage,
                    icon = { Icon(Icons.Outlined.MenuBook, contentDescription = "Reflect") },
                    label = { Text("Reflect", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = OIATaupe,
                        unselectedTextColor = OIAStone
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateSettings,
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = OIATaupe,
                        unselectedTextColor = OIAStone
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ClearStreak",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OIASage
                        )
                        Text(
                            text = "Air-Gapped • Zero Knowledge",
                            fontSize = 13.sp,
                            color = OIAStone
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Crisis Intercept Button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onCrisisTriggered() },
                            color = OIAError.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Crisis Help",
                                    tint = OIAError,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Rescue",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OIAError
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = onLockApp) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock App",
                                tint = OIATaupe
                            )
                        }
                    }
                }
            }

            dailyVerse?.let { v ->
                item {
                    OIACard(
                        backgroundColor = OIAWarmWhite,
                        cornerRadius = 16.dp,
                        padding = 16.dp
                    ) {
                        Text(
                            text = "Verse of the Day",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OIASage
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${v.text}”",
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = OIACharcoal,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— ${v.citation}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OIAStone
                        )
                    }
                }
            }

            dailyPassage?.let { p ->
                item {
                    OIACard(
                        backgroundColor = OIAWarmWhite,
                        cornerRadius = 16.dp,
                        padding = 16.dp
                    ) {
                        Text(
                            text = "Passage of the Day",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OIASage
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = p.surfaceText,
                            fontSize = 15.sp,
                            color = OIACharcoal,
                            lineHeight = 22.sp
                        )
                        if (showFaith && p.faithOptional != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = p.faithOptional,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                color = OIACoral,
                                lineHeight = 19.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— ${p.citation}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OIAStone
                        )
                    }
                }
            }

            if (journeys.isEmpty()) {
                item {
                    EmptyJourneysCard(onAddJourneyClicked = onAddJourneyClicked)
                }
            } else {
                items(journeys) { journey ->
                    val stats = StreakCalculator.calculateStats(journey, checkIns)
                    JourneyCard(
                        journey = journey,
                        stats = stats,
                        onCardClicked = { onJourneySelected(journey) },
                        onCheckInClicked = { onCheckInClicked(journey) }
                    )
                }

                item {
                    OIASecondaryButton(
                        text = "+ Add Another Journey",
                        onClick = onAddJourneyClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(
    journey: Journey,
    stats: com.eight64zeros.clearstreak.model.StreakStats,
    onCardClicked: () -> Unit,
    onCheckInClicked: () -> Unit
) {
    OIACard(
        modifier = Modifier.clickable { onCardClicked() },
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = journey.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Text(
                    text = "${journey.category.emoji} ${journey.categoryLabel}",
                    fontSize = 13.sp,
                    color = OIAStone
                )
            }

            Surface(
                shape = OIARadiusPill,
                color = OIASage.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIASage,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Streak Display
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${stats.currentStreakDays}",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = OIASage,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (stats.currentStreakDays == 1L) "day clear" else "days clear",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = OIACharcoal,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${stats.currentStreakHours}h)",
                fontSize = 14.sp,
                color = OIAStone,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Milestone Progress Bar
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Next milestone: ${stats.nextMilestoneName}",
                    fontSize = 13.sp,
                    color = OIAStone
                )
                Text(
                    text = "${(stats.milestoneProgress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OIASage
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { stats.milestoneProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = OIASage,
                trackColor = OIATaupe.copy(alpha = 0.2f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cumulative & Cost Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Total Days Clear", fontSize = 12.sp, color = OIAStone)
                Text(
                    text = "${stats.cumulativeDaysClear} days",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OIACharcoal
                )
            }

            if (journey.dailyCostSavings > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Money Saved", fontSize = 12.sp, color = OIAStone)
                    Text(
                        text = "$${String.format("%.2f", stats.totalMoneySaved)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OIASage
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OIAPrimaryButton(
            text = "Check In",
            onClick = onCheckInClicked
        )
    }
}

@Composable
private fun EmptyJourneysCard(onAddJourneyClicked: () -> Unit) {
    OIACard(
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 24.dp
    ) {
        Text(
            text = "Welcome to ClearStreak",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OIACharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start tracking your recovery with complete hardware-bound privacy. No accounts, no cloud sync, no tracking.",
            fontSize = 14.sp,
            color = OIAStone,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        OIAPrimaryButton(
            text = "Add Your First Journey",
            onClick = onAddJourneyClicked
        )
    }
}
