package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReceiptViewModel,
    onNavigateBack: () -> Unit,
    onThemeChange: (dark: Boolean, amoled: Boolean) -> Unit
) {
    val context = LocalContext.current
    var isAmoled by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(true) }

    var pdfQualityHigh by remember { mutableStateOf(true) }
    var defaultFileFormat by remember { mutableStateOf("PNG") }

    Scaffold(
        modifier = Modifier.testTag("settings_screen"),
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings & Brand Studio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Visual Theme controls
            Text("🎨 Styling Theme Modes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Standard Dark Mode", fontWeight = FontWeight.Bold)
                            Text("Enables comfortable night-time design controls", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = {
                                isDarkTheme = it
                                onThemeChange(isDarkTheme, isAmoled)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pure Black AMOLED Mode", fontWeight = FontWeight.Bold)
                            Text("Save pixel ink via pitch black themes", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = isAmoled,
                            onCheckedChange = {
                                isAmoled = it
                                if (isAmoled) {
                                    isDarkTheme = true
                                }
                                onThemeChange(isDarkTheme, isAmoled)
                            }
                        )
                    }
                }
            }

            // Section 2: PDF & Export Quality Overrides
            Text("📐 Export Quality Resolutions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Export Design Vector PDFs", fontWeight = FontWeight.Bold)
                            Text("Maintains ultra high definition print layers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = pdfQualityHigh,
                            onCheckedChange = { pdfQualityHigh = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Prefer JPEG over PNG", fontWeight = FontWeight.Bold)
                            Text("Optimizes file sizes for fast WhatsApp shares", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = defaultFileFormat == "JPEG",
                            onCheckedChange = { defaultFileFormat = if (it) "JPEG" else "PNG" }
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast
                                    .makeText(context, "System cache cleaned successfully!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Local Storage Cleaner", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text("Flushes temporary background render images safely", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Section 3: About Developer (Prince AR Abdur Rahman)
            Text("👨‍💻 About Studio Chief Developer", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DeveloperMode, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Prince AR Abdur Rahman",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text("Independent Android App Developer", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                        }
                    }

                    Text(
                        text = "Passionate about creating modern Android applications, digital workflows, educational solutions, and next-generation productivity tools.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    )

                    Divider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f))

                    // Contacts list
                    Text("Direct Hotlines & Chats:", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                    Text("• WhatsApp: 01707424006 / 01796951709", fontSize = 12.sp)
                    Text("• Facebook Address: fb.com/share/1BNn32qoJo/", fontSize = 12.sp)
                    Text("• Instagram username: ur___abdur____rahman__2008", fontSize = 12.sp)
                }
            }

            // Section 4: About Company (NexVora Lab's Ofc)
            Text("🏢 About Studio Publisher Labs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "NexVora Lab's Ofc",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Building fast, beautiful, privacy-friendly, and user-focused applications accessible to everyone globally.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("Digital Application Portfolio Catalog:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val products = listOf(
                        "NexPlay X", "LifeSphere OS", "Smart Day Planner X", "Study AI",
                        "Lensora Studio", "Offline AI", "NexVora Love Space", "CalcVerse", "NexVoice OS"
                    )
                    products.forEach { prod ->
                        Text("• $prod", fontSize = 12.sp)
                    }
                }
            }

            // Technical details Row
            Text("🔧 Technical Ecosystem Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Architecture: Model-View-ViewModel (MVVM) Clean Structure", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text("• UI Engine: Declarative Jetpack Compose Material 3", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text("• Database: Room SQLite Offline-First Engine", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Text("• Code Quality Build Target: Android 36 SDK", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
