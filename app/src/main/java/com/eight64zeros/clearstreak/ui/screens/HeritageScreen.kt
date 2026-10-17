package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.AffirmationStore
import com.eight64zeros.clearstreak.data.FocusPackRepository
import com.eight64zeros.clearstreak.data.HeritageStore
import com.eight64zeros.clearstreak.data.PassageStore
import com.eight64zeros.clearstreak.model.Affirmation
import com.eight64zeros.clearstreak.model.BookPassage
import com.eight64zeros.clearstreak.model.DailyVerse
import com.eight64zeros.clearstreak.model.FocusPackEntry
import com.eight64zeros.clearstreak.model.FocusPacks
import com.eight64zeros.clearstreak.navigation.Screen
import com.eight64zeros.clearstreak.ui.components.ClearStreakBottomBar
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.VerseCalendar
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class Segment { FOCUS_PACKS, SCRIPTURE, RECOVERY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageScreen(
    showFaith: Boolean,
    showAffirmations: Boolean,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    activeFocusPackId: String = FocusPacks.STOIC_MEDITATIONS,
    onSelectFocusPack: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val heritageStore = remember { HeritageStore(context) }
    val passageStore = remember { PassageStore(context) }
    val affirmationStore = remember { AffirmationStore(context) }
    val focusPackRepo = remember { FocusPackRepository(context, heritageStore, passageStore, affirmationStore) }
    val today = remember { LocalDate.now() }
    val dateFmt = remember { DateTimeFormatter.ofPattern("MMMM d") }

    var segment by remember { mutableStateOf(Segment.FOCUS_PACKS) }

    // Focus Pack state
    var focusDate by remember { mutableStateOf(today) }
    val dateFocusEntry = remember(activeFocusPackId, focusDate, showFaith, focusPackRepo) {
        focusPackRepo.entryForDate(activeFocusPackId, focusDate, showFaith)
    }
    var randomFocusEntry by remember(activeFocusPackId) { mutableStateOf<FocusPackEntry?>(null) }

    // Scripture state
    var verseDate by remember { mutableStateOf(today) }
    val dateVerse = remember(verseDate, heritageStore) { heritageStore.verseForDate(verseDate) }
    var randomVerse by remember { mutableStateOf<DailyVerse?>(null) }
    var spiritualAff by remember { mutableStateOf(affirmationStore.randomSpiritual()) }

    // Recovery state (mirrors Scripture)
    var passageDate by remember { mutableStateOf(today) }
    val datePassage = remember(passageDate, passageStore) { passageStore.passageForDate(passageDate) }
    var randomPassage by remember { mutableStateOf<BookPassage?>(null) }
    var recoveryAff by remember { mutableStateOf(affirmationStore.randomRecovery()) }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = { Text("Reflect & Focus Packs", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OIACharcoal) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OIACharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OIACream)
            )
        },
        bottomBar = {
            ClearStreakBottomBar(current = Screen.Heritage.route, onNavigate = onNavigate)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentChip("🏛️ Focus Packs", segment == Segment.FOCUS_PACKS, Modifier.weight(1f)) {
                    segment = Segment.FOCUS_PACKS
                }
                SegmentChip("📖 Scripture", segment == Segment.SCRIPTURE, Modifier.weight(1f)) {
                    segment = Segment.SCRIPTURE
                }
                SegmentChip("🕊️ Recovery", segment == Segment.RECOVERY, Modifier.weight(1f)) {
                    segment = Segment.RECOVERY
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (segment) {
                Segment.FOCUS_PACKS -> FocusPacksSection(
                    activeFocusPackId = activeFocusPackId,
                    onSelectFocusPack = onSelectFocusPack,
                    today = today,
                    selectedDate = focusDate,
                    dateFmt = dateFmt,
                    dateEntry = dateFocusEntry,
                    onSelectDate = { focusDate = it },
                    randomEntry = randomFocusEntry,
                    onRandomEntry = { randomFocusEntry = focusPackRepo.randomEntry(activeFocusPackId, showFaith) }
                )
                Segment.SCRIPTURE -> ScriptureSection(
                    today = today,
                    selectedDate = verseDate,
                    dateFmt = dateFmt,
                    dateVerse = dateVerse,
                    onSelectDate = { verseDate = it },
                    randomVerse = randomVerse,
                    onRandomVerse = { randomVerse = heritageStore.randomVerse() },
                    showAffirmations = showAffirmations,
                    affirmation = spiritualAff,
                    onRandomAffirmation = { spiritualAff = affirmationStore.randomSpiritual() }
                )
                Segment.RECOVERY -> RecoverySection(
                    showFaith = showFaith,
                    today = today,
                    selectedDate = passageDate,
                    dateFmt = dateFmt,
                    datePassage = datePassage,
                    onSelectDate = { passageDate = it },
                    randomPassage = randomPassage,
                    onRandomPassage = { randomPassage = passageStore.random() },
                    showAffirmations = showAffirmations,
                    affirmation = recoveryAff,
                    onRandomAffirmation = { recoveryAff = affirmationStore.randomRecovery() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FocusPacksSection(
    activeFocusPackId: String,
    onSelectFocusPack: (String) -> Unit,
    today: LocalDate,
    selectedDate: LocalDate,
    dateFmt: DateTimeFormatter,
    dateEntry: FocusPackEntry?,
    onSelectDate: (LocalDate) -> Unit,
    randomEntry: FocusPackEntry?,
    onRandomEntry: () -> Unit
) {
    val activeMeta = FocusPacks.metaFor(activeFocusPackId)

    ContentCard(
        title = "Compartmentalized Focus Packs",
        subtitle = "Choose which philosophy or wisdom voice powers your daily card so worldviews never clash."
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FocusPacks.ALL.forEach { pack ->
                val isSelected = pack.id == activeFocusPackId
                Surface(
                    modifier = Modifier.clickable { onSelectFocusPack(pack.id) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) OIASage.copy(alpha = 0.2f) else OIACream.copy(alpha = 0.6f),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) OIASage else OIATaupe.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = "${pack.emoji} ${pack.title}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = OIACharcoal
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${activeMeta.emoji} Active Voice: ${activeMeta.title} — ${activeMeta.subtitle}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIASage
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(
        title = if (selectedDate == today) "Daily Focus Card" else "Focus Card for ${selectedDate.format(dateFmt)}",
        subtitle = activeMeta.title
    ) {
        if (dateEntry == null) EmptyLine("Focus Pack entry unavailable.")
        else FocusEntryBody(dateEntry)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Browse by Date", subtitle = "Tap any day to read its Focus Pack entry above.") {
        VerseCalendar(selectedDate = selectedDate, onSelectDate = onSelectDate)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Random Draw from ${activeMeta.title}") {
        OIAPrimaryButton(text = "🎲 Draw another from this pack", onClick = onRandomEntry)
        randomEntry?.let {
            Spacer(modifier = Modifier.height(16.dp))
            FocusEntryBody(it)
        }
    }
}

@Composable
private fun FocusEntryBody(entry: FocusPackEntry) {
    Text("“${entry.text}”", fontSize = 16.sp, fontStyle = FontStyle.Italic, color = OIACharcoal, lineHeight = 24.sp)
    Spacer(modifier = Modifier.height(10.dp))
    Text(entry.plain, fontSize = 14.sp, color = OIACharcoal, lineHeight = 21.sp)
    Spacer(modifier = Modifier.height(10.dp))
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = OIASage.copy(alpha = 0.12f)
    ) {
        Text(
            text = "🎯 Today's Practice: ${entry.application}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = OIACharcoal,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text("— ${entry.citation}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
}

@Composable
private fun ScriptureSection(
    today: LocalDate,
    selectedDate: LocalDate,
    dateFmt: DateTimeFormatter,
    dateVerse: DailyVerse?,
    onSelectDate: (LocalDate) -> Unit,
    randomVerse: DailyVerse?,
    onRandomVerse: () -> Unit,
    showAffirmations: Boolean,
    affirmation: Affirmation?,
    onRandomAffirmation: () -> Unit
) {
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text("The Serenity Prayer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Spacer(modifier = Modifier.height(10.dp))
        Text(HeritageStore.SERENITY_PRAYER, fontSize = 16.sp, fontStyle = FontStyle.Italic, color = OIACharcoal, lineHeight = 24.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(
        title = if (selectedDate == today) "Verse of the Day" else "Verse for ${selectedDate.format(dateFmt)}"
    ) {
        if (dateVerse == null) EmptyLine("Verse content is unavailable.")
        else QuoteBody(dateVerse.text, dateVerse.citation)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Browse by Date", subtitle = "Tap any day to read its verse above.") {
        VerseCalendar(selectedDate = selectedDate, onSelectDate = onSelectDate)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Random Verse") {
        OIAPrimaryButton(text = "🎲 Give me a verse", onClick = onRandomVerse)
        randomVerse?.let {
            Spacer(modifier = Modifier.height(16.dp))
            QuoteBody(it.text, it.citation)
        }
    }

    if (showAffirmations) {
        Spacer(modifier = Modifier.height(16.dp))
        ContentCard(title = "Affirmation", subtitle = "A word for you — original, scripture-rooted.") {
            affirmation?.let {
                AffirmationBody(it)
                Spacer(modifier = Modifier.height(16.dp))
            } ?: EmptyLine("No affirmation available.")
            OIAPrimaryButton(text = "🎲 Another affirmation", onClick = onRandomAffirmation)
        }
    }
}

@Composable
private fun RecoverySection(
    showFaith: Boolean,
    today: LocalDate,
    selectedDate: LocalDate,
    dateFmt: DateTimeFormatter,
    datePassage: BookPassage?,
    onSelectDate: (LocalDate) -> Unit,
    randomPassage: BookPassage?,
    onRandomPassage: () -> Unit,
    showAffirmations: Boolean,
    affirmation: Affirmation?,
    onRandomAffirmation: () -> Unit
) {
    Text(
        text = "Plain-language passages re-authored from the 1939 recovery classic. One for each day; faith is optional and never assumed.",
        fontSize = 13.sp,
        color = OIAStone
    )

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(
        title = if (selectedDate == today) "Passage of the Day" else "Passage for ${selectedDate.format(dateFmt)}"
    ) {
        if (datePassage == null) EmptyLine("No passage available.")
        else PassageBody(datePassage, showFaith)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Browse by Date", subtitle = "Tap any day to read its passage above.") {
        VerseCalendar(selectedDate = selectedDate, onSelectDate = onSelectDate)
    }

    Spacer(modifier = Modifier.height(16.dp))
    ContentCard(title = "Random Passage") {
        OIAPrimaryButton(text = "🎲 Give me a passage", onClick = onRandomPassage)
        randomPassage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            PassageBody(it, showFaith)
        }
    }

    if (showAffirmations) {
        Spacer(modifier = Modifier.height(16.dp))
        ContentCard(title = "Affirmation", subtitle = "A word for you — original, recovery-rooted.") {
            affirmation?.let {
                AffirmationBody(it)
                Spacer(modifier = Modifier.height(16.dp))
            } ?: EmptyLine("No affirmation available.")
            OIAPrimaryButton(text = "🎲 Another affirmation", onClick = onRandomAffirmation)
        }
    }
}

@Composable
private fun ContentCard(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = OIAStone)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun QuoteBody(text: String, citation: String) {
    Text("“$text”", fontSize = 17.sp, fontStyle = FontStyle.Italic, color = OIACharcoal, lineHeight = 25.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Text("— $citation", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
}

@Composable
private fun PassageBody(passage: BookPassage, showFaith: Boolean) {
    Text(passage.surfaceText, fontSize = 16.sp, color = OIACharcoal, lineHeight = 24.sp)
    if (showFaith && passage.faithOptional != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(passage.faithOptional, fontSize = 14.sp, fontStyle = FontStyle.Italic, color = OIACoral, lineHeight = 20.sp)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text("— ${passage.citation}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
}

@Composable
private fun AffirmationBody(affirmation: Affirmation) {
    Text(affirmation.text, fontSize = 17.sp, color = OIACharcoal, lineHeight = 25.sp)
    if (affirmation.scriptureText != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "“${affirmation.scriptureText}”",
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            color = OIACoral,
            lineHeight = 19.sp
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text("— ${affirmation.citation}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
}

@Composable
private fun EmptyLine(text: String) {
    Text(text, fontSize = 13.sp, color = OIAStone)
}

@Composable
private fun SegmentChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) OIASage else OIATaupe.copy(alpha = 0.4f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = OIACharcoal
            )
        }
    }
}
