package com.eight64zeros.clearstreak.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.EmergencyContacts
import com.eight64zeros.clearstreak.ui.components.BreathingCircle
import com.eight64zeros.clearstreak.ui.components.GroundingTimer
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.OIASecondaryButton
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIADarkBg
import com.eight64zeros.clearstreak.ui.theme.OIADarkCard
import com.eight64zeros.clearstreak.ui.theme.OIADarkElevated
import com.eight64zeros.clearstreak.ui.theme.OIADustyBlue
import com.eight64zeros.clearstreak.ui.theme.OIAError
import com.eight64zeros.clearstreak.ui.theme.OIAOffWhite
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIASageDark
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmGray
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CrisisInterceptScreen(
    contacts: EmergencyContacts,
    onSafeReturn: () -> Unit
) {
    val context = LocalContext.current
    var activeTool by remember { mutableStateOf<String?>("breathing") } // "breathing", "grounding", null

    // Safe return long-press state (2 seconds)
    var isPressingSafe by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    var pressJob by remember { mutableStateOf<Job?>(null) }

    fun dialNumber(number: String) {
        if (number.isBlank()) {
            Toast.makeText(context, "No phone number configured in Settings.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.trim()}"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to launch dialer.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMeetingFinder() {
        try {
            // Inclusive of all recovery types (alcohol, gambling, substances, behavioral)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=recovery+support+meeting+near+me"))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to launch maps.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(number: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${number.trim()}"))
            intent.putExtra("sms_body", body)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open messaging app.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWeb(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to open link.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OIADarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High-contrast Header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OIAError.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Crisis",
                        tint = OIAError,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "STOP. TAKE ONE BREATH.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OIAOffWhite
                        )
                        Text(
                            text = "You do not have to act on this urge.",
                            fontSize = 14.sp,
                            color = OIAWarmGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Dialers
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sponsor + support person are always shown (adding them is key);
                // when unset, they prompt the user to configure a number in Settings.
                RescueDialerButton(
                    label = "📞 CALL ${if (contacts.sponsorPhone.isNotBlank()) contacts.sponsorName.uppercase() else "SPONSOR"}",
                    sublabel = if (contacts.sponsorPhone.isNotBlank()) contacts.sponsorPhone else "Not set — add in Settings",
                    color = OIASage,
                    onClick = { dialNumber(contacts.sponsorPhone) }
                )

                RescueDialerButton(
                    label = "👥 CALL ${if (contacts.supportPersonPhone.isNotBlank()) contacts.supportPersonName.uppercase() else "SUPPORT PERSON"}",
                    sublabel = if (contacts.supportPersonPhone.isNotBlank()) contacts.supportPersonPhone else "Not set — add in Settings",
                    color = OIADustyBlue,
                    onClick = { dialNumber(contacts.supportPersonPhone) }
                )

                // National Helplines
                RescueDialerButton(
                    label = "🆘 SAMHSA HELPLINE (24/7)",
                    sublabel = "1-800-662-4357 (Free, Confidential)",
                    color = OIACoral,
                    onClick = { dialNumber("18006624357") }
                )

                RescueDialerButton(
                    label = "🆘 988 CRISIS LIFELINE",
                    sublabel = "Call or Text 988",
                    color = OIACoral,
                    onClick = { dialNumber("988") }
                )

                RescueDialerButton(
                    label = "💬 CRISIS TEXT LINE",
                    sublabel = "Text HOME to 741741",
                    color = OIACoral,
                    icon = Icons.Default.Sms,
                    onClick = { sendSms("741741", "HOME") }
                )

                // Meeting Finder
                RescueDialerButton(
                    label = "📍 FIND A MEETING NOW",
                    sublabel = "Opens native map without saving location",
                    color = OIADustyBlue,
                    onClick = { openMeetingFinder() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "More support",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = OIAWarmGray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RescueDialerButton(
                    label = "🚭 QUIT SMOKING HELPLINE",
                    sublabel = "1-800-QUIT-NOW (1-800-784-8669)",
                    color = OIASage,
                    onClick = { dialNumber("18007848669") }
                )
                RescueDialerButton(
                    label = "🚭 SMOKEFREE.GOV",
                    sublabel = "Chat or speak to a quit expert",
                    color = OIADustyBlue,
                    icon = Icons.Default.Language,
                    onClick = { openWeb("https://smokefree.gov/tools-tips/get-extra-help/speak-to-an-expert") }
                )
                RescueDialerButton(
                    label = "🎲 PROBLEM GAMBLING HELPLINE",
                    sublabel = "1-800-MY-RESET (1-800-697-3738)",
                    color = OIASage,
                    onClick = { dialNumber("18006973738") }
                )
                RescueDialerButton(
                    label = "🎲 NCPGAMBLING.ORG",
                    sublabel = "Chat with a counselor",
                    color = OIADustyBlue,
                    icon = Icons.Default.Language,
                    onClick = { openWeb("https://www.ncpgambling.org") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grounding & Somatic Tools Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { activeTool = if (activeTool == "breathing") null else "breathing" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTool == "breathing") OIASage else OIADarkElevated,
                        contentColor = OIAWarmWhite
                    )
                ) {
                    Text("🌬️ 4-7-8 Breathing", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { activeTool = if (activeTool == "grounding") null else "grounding" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTool == "grounding") OIASage else OIADarkElevated,
                        contentColor = OIAWarmWhite
                    )
                ) {
                    Text("🫁 60s Grounding", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Grounding Tool Container
            AnimatedVisibility(visible = activeTool != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = OIADarkCard
                ) {
                    Box(
                        modifier = Modifier.padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (activeTool) {
                            "breathing" -> BreathingCircle()
                            "grounding" -> GroundingTimer(onFinished = {})
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Safe Exit with 2-second Long-Press confirmation
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Press and hold for 2 seconds to confirm you are safe",
                    fontSize = 13.sp,
                    color = OIAWarmGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OIASageDark)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressingSafe = true
                                    pressJob = scope.launch {
                                        val startTime = System.currentTimeMillis()
                                        while (System.currentTimeMillis() - startTime < 2000) {
                                            val elapsed = System.currentTimeMillis() - startTime
                                            holdProgress = (elapsed / 2000f).coerceIn(0f, 1f)
                                            delay(50)
                                        }
                                        holdProgress = 1f
                                        onSafeReturn()
                                    }
                                    tryAwaitRelease()
                                    isPressingSafe = false
                                    pressJob?.cancel()
                                    holdProgress = 0f
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Progress overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OIASage.copy(alpha = holdProgress))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Safe",
                            tint = OIAWarmWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (holdProgress > 0f) "HOLDING... ${(holdProgress * 100).toInt()}%" else "I AM SAFE — RETURN TO APP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OIAWarmWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RescueDialerButton(
    label: String,
    sublabel: String,
    color: Color,
    icon: ImageVector = Icons.Default.Phone,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = OIAWarmWhite
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIAOffWhite
                )
                Text(
                    text = sublabel,
                    fontSize = 12.sp,
                    color = OIAWarmGray
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        }
    }
}
