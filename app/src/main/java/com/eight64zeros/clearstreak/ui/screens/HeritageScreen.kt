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
import androidx.compose.runtime.LaunchedEffect
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
import com.eight64zeros.clearstreak.data.HeritageStore
import com.eight64zeros.clearstreak.data.PassageStore
import com.eight64zeros.clearstreak.model.BookPassage
import com.eight64zeros.clearstreak.model.DailyVerse
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.VerseCalendar
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class Segment { SCRIPTURE, RECOVERY }

// Browse themes for the recovery passages -> moment key (null = all).
private val PASSAGE_THEMES = listOf(
    "All" to null,
    "Craving" to "craving-now",
    "Anger" to "resentment",
    "Fear" to "fear",
    "After a slip" to "after-a-slip",
    "Staying strong" to "staying-the-course",
    "Starting out" to "starting-out"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val heritageStore = remember { HeritageStore(context) }
    val passageStore = remember { PassageStore(context) }
    val today = remember { LocalDate.now() }
    val dateFmt = remember { DateTimeFormatter.ofPattern("MMMM d") }

    var segment by remember { mutableStateOf(Segment.SCRIPTURE) }

    // Scripture state
    var selectedDate by remember { mutableStateOf(today) }
    val dateVerse = remember(selectedDate, heritageStore) { heritageStore.verseForDate(selectedDate) }
    var randomVerse by remember { mutableStateOf<DailyVerse?>(null) }

    // Recovery state
    var theme by remember { mutableStateOf("All") }
    var passage by remember { mutableStateOf<BookPassage?>(null) }
    fun poolFor(t: String): List<BookPassage> {
        val moment = PASSAGE_THEMES.firstOrNull { it.first == t }?.second
        return if (moment == null) passageStore.passages else passageStore.forMoment(moment)
    }
    LaunchedEffect(segment, theme) {
        if (segment == Segment.RECOVERY) passage = poolFor(theme).randomOrNull()
    }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text("Reflect", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OIACharcoal)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OIACharcoal)
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Segment toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentChip("📖 Scripture", segment == Segment.SCRIPTURE, Modifier.weight(1f)) {
                    segment = Segment.SCRIPTURE
                }
                SegmentChip("🕊️ Recovery", segment == Segment.RECOVERY, Modifier.weight(1f)) {
                    segment = Segment.RECOVERY
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (segment) {
                Segment.SCRIPTURE -> ScriptureSection(
                    today = today,
                    selectedDate = selectedDate,
                    dateFmt = dateFmt,
                    dateVerse = dateVerse,
                    onSelectDate = { selectedDate = it },
                    randomVerse = randomVerse,
                    onRandomVerse = { randomVerse = heritageStore.randomVerse() }
                )
                Segment.RECOVERY -> RecoverySection(
                    theme = theme,
                    onSelectTheme = { theme = it },
                    passage = passage,
                    onAnother = { passage = poolFor(theme).randomOrNull() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ScriptureSection(
    today: LocalDate,
    selectedDate: LocalDate,
    dateFmt: DateTimeFormatter,
    dateVerse: DailyVerse?,
    onSelectDate: (LocalDate) -> Unit,
    randomVerse: DailyVerse?,
    onRandomVerse: () -> Unit
) {
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text("The Serenity Prayer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = HeritageStore.SERENITY_PRAYER,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = OIACharcoal,
            lineHeight = 24.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
    VerseCard(
        title = if (selectedDate == today) "Verse of the Day" else "Verse for ${selectedDate.format(dateFmt)}",
        verse = dateVerse
    )

    Spacer(modifier = Modifier.height(16.dp))
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 16.dp) {
        Text("Browse by Date", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Text("Tap any day to read its verse above.", fontSize = 12.sp, color = OIAStone)
        Spacer(modifier = Modifier.height(12.dp))
        VerseCalendar(selectedDate = selectedDate, onSelectDate = onSelectDate)
    }

    Spacer(modifier = Modifier.height(16.dp))
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text("Random Verse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        OIAPrimaryButton(text = "🎲 Give me a verse", onClick = onRandomVerse)
        randomVerse?.let { v ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("“${v.text}”", fontSize = 17.sp, fontStyle = FontStyle.Italic, color = OIACharcoal, lineHeight = 25.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("— ${v.citation}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecoverySection(
    theme: String,
    onSelectTheme: (String) -> Unit,
    passage: BookPassage?,
    onAnother: () -> Unit
) {
    Text(
        text = "Modern, plain-language passages re-authored from the 1939 recovery classic. Faith is optional and never assumed.",
        fontSize = 13.sp,
        color = OIAStone
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Theme chips (wrap)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PASSAGE_THEMES.forEach { (label, _) ->
            SegmentChip(label, theme == label, Modifier) { onSelectTheme(label) }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        if (passage == null) {
            Text("No passage available.", fontSize = 13.sp, color = OIAStone)
        } else {
            Text(passage.surfaceText, fontSize = 16.sp, color = OIACharcoal, lineHeight = 24.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text("— ${passage.citation}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OIAPrimaryButton(text = "🎲 Another passage", onClick = onAnother)
    }
}

@Composable
private fun VerseCard(title: String, verse: DailyVerse?) {
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        if (verse == null) {
            Text("Verse content is unavailable.", fontSize = 13.sp, color = OIAStone)
        } else {
            Text("“${verse.text}”", fontSize = 17.sp, fontStyle = FontStyle.Italic, color = OIACharcoal, lineHeight = 25.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("— ${verse.citation}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OIASage)
        }
    }
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
