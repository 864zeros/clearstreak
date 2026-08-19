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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScienceScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The Science",
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
                text = "Every tool here is built on how the mind and body actually respond to cravings and stress. Here's the plain version — and the research it draws on. This is background, not medical advice or treatment.",
                fontSize = 14.sp,
                color = OIAStone,
                lineHeight = 20.sp
            )

            ScienceSection(
                icon = "🧩",
                title = "The puzzle games",
                plain = "A craving works partly by picturing the thing you want in your mind's eye. A visually demanding puzzle uses that same mental \"screen,\" so there's less room to hold the craving image — and it tends to weaken and fade faster. Researchers call the underlying idea Elaborated Intrusion theory, and studies have found that a few minutes of a Tetris-style game measurably lowers the strength and vividness of everyday cravings.",
                references = listOf(
                    "Kavanagh, Andrade & May (2005). The elaborated intrusion theory of desire. Psychological Review, 112(2), 446–467.",
                    "Skorka-Brown, Andrade & May (2014). Playing 'Tetris' reduces the strength, frequency and vividness of naturally occurring cravings. Appetite, 76, 161–165.",
                    "Skorka-Brown, Andrade, Whalley & May (2015). Playing Tetris decreases drug and other cravings in real world settings. Addictive Behaviors, 51, 165–170."
                )
            )

            ScienceSection(
                icon = "🌬️",
                title = "The breathing exercises",
                plain = "Slow, even breathing — especially breathing out for longer than you breathe in — signals your body to shift out of fight-or-flight and into a calmer state. That lowers the physical arousal (racing heart, tension) that makes an urge feel urgent. Reviews of slow-breathing research link it to greater calm, better attention, and healthier heart-rate variability.",
                references = listOf(
                    "Zaccaro et al. (2018). How breath-control can change your life: a systematic review on psycho-physiological correlates of slow breathing. Frontiers in Human Neuroscience, 12, 353.",
                    "Ma et al. (2017). The effect of diaphragmatic breathing on attention, negative affect and stress in healthy adults. Frontiers in Psychology, 8, 874."
                )
            )

            ScienceSection(
                icon = "🫁",
                title = "The Pocket Anchor",
                plain = "A craving rises, peaks, and passes like a wave — usually within minutes. A steady pulse gives you a simple, external rhythm to ride it out instead of acting on it. This \"urge surfing\" approach comes from mindfulness-based relapse prevention: you notice the urge, stay with the sensation, and let it crest and subside on its own.",
                references = listOf(
                    "Bowen, Chawla & Marlatt (2011). Mindfulness-Based Relapse Prevention for Addictive Behaviors. Guilford Press.",
                    "Marlatt & Gordon (1985). Relapse Prevention. Guilford Press."
                )
            )

            OIACard(
                backgroundColor = OIASage.copy(alpha = 0.10f),
                borderColor = OIASage.copy(alpha = 0.3f),
                cornerRadius = 16.dp,
                padding = 16.dp
            ) {
                Text(
                    text = "A note",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIASage
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "These tools can help you get through a moment — they are not a treatment or a cure, and they aren't a substitute for professional care. If you're in crisis, use the Rescue screen or call or text 988.",
                    fontSize = 13.sp,
                    color = OIACharcoal,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ScienceSection(
    icon: String,
    title: String,
    plain: String,
    references: List<String>
) {
    OIACard(
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 20.dp
    ) {
        Text(
            text = "$icon  $title",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OIACharcoal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = plain,
            fontSize = 14.sp,
            color = OIACharcoal,
            lineHeight = 21.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "References",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIASage
        )
        Spacer(modifier = Modifier.height(4.dp))
        references.forEach { ref ->
            Text(
                text = "• $ref",
                fontSize = 12.sp,
                color = OIAStone,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
