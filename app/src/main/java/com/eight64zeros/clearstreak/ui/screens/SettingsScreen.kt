package com.eight64zeros.clearstreak.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.EmergencyContacts
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.OIASecondaryButton
import com.eight64zeros.clearstreak.ui.components.OIATextField
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contacts: EmergencyContacts,
    onSaveContacts: (EmergencyContacts) -> Unit,
    showVerseOnHome: Boolean,
    onToggleVerseOnHome: (Boolean) -> Unit,
    onLockApp: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var sponsorName by remember { mutableStateOf(contacts.sponsorName) }
    var sponsorPhone by remember { mutableStateOf(contacts.sponsorPhone) }
    var supportName by remember { mutableStateOf(contacts.supportPersonName) }
    var supportPhone by remember { mutableStateOf(contacts.supportPersonPhone) }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Emergency",
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
            // Emergency Contacts Section
            item {
                Text(
                    text = "Rescue Contacts (Crisis Intercept)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configured phone numbers appear as one-tap dialers during Crisis Intercept.",
                    fontSize = 13.sp,
                    color = OIAStone
                )
            }

            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Text(text = "Sponsor Contact", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = OIACharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    OIATextField(
                        value = sponsorName,
                        onValueChange = { sponsorName = it },
                        placeholder = "Sponsor Name / Nickname"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OIATextField(
                        value = sponsorPhone,
                        onValueChange = { sponsorPhone = it },
                        placeholder = "Phone Number (e.g. 555-123-4567)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(text = "Personal Support Contact", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = OIACharcoal)
                    Spacer(modifier = Modifier.height(10.dp))
                    OIATextField(
                        value = supportName,
                        onValueChange = { supportName = it },
                        placeholder = "Support Person Name"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OIATextField(
                        value = supportPhone,
                        onValueChange = { supportPhone = it },
                        placeholder = "Phone Number (e.g. 555-987-6543)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OIAPrimaryButton(
                        text = "Save Contacts",
                        onClick = {
                            onSaveContacts(
                                EmergencyContacts(
                                    sponsorName = sponsorName.ifBlank { "Sponsor" },
                                    sponsorPhone = sponsorPhone,
                                    supportPersonName = supportName.ifBlank { "Support Person" },
                                    supportPersonPhone = supportPhone
                                )
                            )
                            Toast.makeText(context, "Contacts saved.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Home Screen preferences
            item {
                Text(
                    text = "Home Screen",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
            }
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show today's verse on Home",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OIACharcoal
                            )
                            Text(
                                text = "Display the daily scripture verse at the top of your home screen.",
                                fontSize = 13.sp,
                                color = OIAStone
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(checked = showVerseOnHome, onCheckedChange = onToggleVerseOnHome)
                    }
                }
            }

            // Your Privacy (plain language)
            item {
                Text(
                    text = "Your Privacy",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "What's built in — and what it means for you.",
                    fontSize = 13.sp,
                    color = OIAStone
                )
            }
            item {
                OIACard(
                    backgroundColor = OIAWarmWhite,
                    cornerRadius = 16.dp,
                    padding = 16.dp
                ) {
                    SecurityItem("✈️", "It works completely offline", "The app has no internet access at all, so nothing you write can ever be sent, shared, or leaked online.")
                    SecurityItem("🔒", "Your journal is encrypted", "Everything you record is scrambled with strong encryption and stored only on this phone.")
                    SecurityItem("👆", "Only you can unlock it", "The key is tied to your fingerprint or face and this phone's secure chip — there's no password anyone (including us) could recover or be forced to hand over.")
                    SecurityItem("☁️", "Nothing goes to the cloud", "Your recovery data is never backed up to any cloud, account, or server. If it isn't on this phone, it doesn't exist.")
                    SecurityItem("🙈", "Screenshots are blocked", "The app stops screenshots and hides your screen when you switch apps, so nothing shows up by accident.")
                }
            }

            // 864zeros Manifesto
            item {
                OIACard(
                    backgroundColor = OIASage.copy(alpha = 0.12f),
                    borderColor = OIASage.copy(alpha = 0.3f),
                    cornerRadius = 16.dp,
                    padding = 18.dp
                ) {
                    Text(
                        text = "864zeros LLC • Core Promise",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIASage
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "“Your recovery is yours alone. We cannot see it, sell it, subpoena it, or lose it.”",
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = OIACharcoal,
                        lineHeight = 20.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                OIASecondaryButton(
                    text = "Lock App Now",
                    onClick = onLockApp,
                    contentColor = OIACharcoal,
                    borderColor = OIATaupe
                )
            }
        }
    }
}

@Composable
private fun SecurityItem(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OIACharcoal)
            Text(text = description, fontSize = 13.sp, color = OIAStone, lineHeight = 18.sp)
        }
    }
}
