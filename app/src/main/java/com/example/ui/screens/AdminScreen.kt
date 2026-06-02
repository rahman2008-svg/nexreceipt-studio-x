package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: ReceiptViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val adminMetrics by viewModel.adminMetrics.collectAsStateWithLifecycle()
    val receiptsList by viewModel.receipts.collectAsStateWithLifecycle()

    // Global Settings Toggles
    var isMaintenanceMode by remember { mutableStateOf(false) }
    var scaleNotifications by remember { mutableStateOf(true) }
    var cloudSyncAutomatic by remember { mutableStateOf(true) }

    // Mock Users database for management
    var usersMockDatabase by remember {
        mutableStateOf(
            listOf(
                AdminUser("princearabdurrahman57@gmail.com", "Prince AR Abdur Rahman", "NexVora Lab's Ofc", "SUPER_ADMIN", false),
                AdminUser("designer@nexvora.com", "Syed Kamal", "Creative Inks Ltd", "USER", false),
                AdminUser("billing@netflix.com", "Alice Rogers", "Netflix Payments", "USER", false),
                AdminUser("guest@nexreceipt.com", "Guest User", "My Business", "USER", false),
                AdminUser("spammer@spam.org", "Ad-Bot Botter", "Fakers Co", "USER", true)
            )
        )
    }

    Scaffold(
        modifier = Modifier.testTag("admin_screen"),
        topBar = {
            MediumTopAppBar(
                title = { Text("Super Admin Control Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
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
            // Metrics Section Card
            Text("📊 Studio Global Analytics & KPIs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Users", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${adminMetrics.totalUsers}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("+12% this month", fontSize = 10.sp, color = Color.Green)
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Receipts Issued", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${adminMetrics.totalReceipts}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("All saved locally", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Exports", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        Text("${adminMetrics.totalExports}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Direct PNG/JPG", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // Trend Chart representation via Canvas or Row columns representing statistics
            Text("📈 Monthly Export Trends (Simulated)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val trends = listOf(20, 45, 30, 80, 65, 95, 120)
                        val months = listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun")
                        
                        trends.forEachIndexed { i, value ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height((value).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(months[i], fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }

            // User Management Section
            Text("🛡️ User Profiles Directory", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    usersMockDatabase.forEach { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (user.role == "SUPER_ADMIN") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.name.take(1).uppercase(), color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${user.email} • ${user.role}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                            // Suspend/Unsuspend trigger
                            Button(
                                onClick = {
                                    usersMockDatabase = usersMockDatabase.map {
                                        if (it.email == user.email) it.copy(isSuspended = !it.isSuspended) else it
                                    }
                                    Toast.makeText(context, "${user.name} update successful!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isSuspended) Color.Green else Color.Red.copy(alpha = 0.8f)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (user.isSuspended) "Unban" else "Suspend",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // App Overrides & Telemetry overrides keys
            Text("⚙️ Super Admin Telemetry Overrides", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Maintenance Mode", fontWeight = FontWeight.Bold)
                            Text("Puts the receipt studio in offline cache read", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = isMaintenanceMode,
                            onCheckedChange = { isMaintenanceMode = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Broadcasting Push Notifications", fontWeight = FontWeight.Bold)
                            Text("Broadcasting messages instantly", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = scaleNotifications,
                            onCheckedChange = { scaleNotifications = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Cloud Sync & Validation", fontWeight = FontWeight.Bold)
                            Text("Upload compiled template layouts securely", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = cloudSyncAutomatic,
                            onCheckedChange = { cloudSyncAutomatic = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class AdminUser(
    val email: String,
    val name: String,
    val company: String,
    val role: String,
    val isSuspended: Boolean
)
