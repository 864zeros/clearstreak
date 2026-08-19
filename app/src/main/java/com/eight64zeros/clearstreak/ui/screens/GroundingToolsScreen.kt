package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.PassageStore
import com.eight64zeros.clearstreak.ui.components.BoxBreather
import com.eight64zeros.clearstreak.ui.components.BreathingCircle
import com.eight64zeros.clearstreak.ui.components.MiniGamesCard
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.PocketAnchor
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroundingToolsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val passageStore = remember { PassageStore(context) }
    var passage by remember { mutableStateOf(passageStore.oneForState("WHITE_KNUCKLING", "GENERAL")) }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Grounding & Reset",
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Eyes-free tools to ride out the wave. No screens to read — just follow the pulse.",
                fontSize = 14.sp,
                color = OIAStone
            )

            // 1) Pocket Anchor (top)
            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🫁 Pocket Anchor",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A steady pulse every minute gives your attention something simple to hold onto while a craving crests and fades.",
                    fontSize = 13.sp,
                    color = OIAStone
                )
                Spacer(modifier = Modifier.height(12.dp))
                PocketAnchor()
            }

            // 2) Games
            MiniGamesCard()

            // 3) Breathing exercises
            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🌬️ 4-4-4-4 Box Breather",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Slow, even breathing calms your nervous system and eases the tension that feeds an urge.",
                    fontSize = 13.sp,
                    color = OIAStone
                )
                Spacer(modifier = Modifier.height(16.dp))
                BoxBreather()
            }

            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🌬️ 4-7-8 Breathing",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Breathing out for longer than you breathe in nudges your body out of fight-or-flight and toward calm.",
                    fontSize = 13.sp,
                    color = OIAStone
                )
                Spacer(modifier = Modifier.height(16.dp))
                BreathingCircle(onLight = true)
            }

            // Words for this moment (a re-authored recovery passage)
            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🕊️ Words for this moment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                passage?.let { p ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = p.surfaceText, fontSize = 15.sp, color = OIACharcoal, lineHeight = 23.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "— ${p.citation}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
                }
                Spacer(modifier = Modifier.height(14.dp))
                OIAPrimaryButton(
                    text = "🎲 Another",
                    onClick = { passage = passageStore.oneForState("WHITE_KNUCKLING", "GENERAL") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
