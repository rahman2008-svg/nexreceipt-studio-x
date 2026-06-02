package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppConstants
import com.example.data.Receipt
import com.example.ui.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ReceiptViewModel,
    onNavigateToEditor: (receiptId: Long?) -> Unit,
    onNavigateToPreview: (receiptId: Long) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val filteredReceipts by viewModel.filteredReceipts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFolder by viewModel.selectedFolder.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val adminMetrics by viewModel.adminMetrics.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.clearEditingItems() // Clear previous editor draft state
                    onNavigateToEditor(null) 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_create_receipt")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Quick Create")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                    label = { Text("Studio") }
                )
                if (activeProfile?.role == "SUPER_ADMIN") {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToAdmin,
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin") }
                    )
                }
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Section 1: Dynamic Top Profile Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Initials Circle
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    val initials = if (!activeProfile?.name.isNullOrEmpty()) {
                        activeProfile!!.name.take(2).uppercase()
                    } else "G"
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Hello, ${activeProfile?.name ?: "Guest User"}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (activeProfile?.role == "SUPER_ADMIN") {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            },
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = if (activeProfile?.role == "SUPER_ADMIN") "Studio Creator" else "Designer Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeProfile?.role == "SUPER_ADMIN") {
                                        MaterialTheme.colorScheme.onTertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    }
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.logout()
                        onNavigateToAuth()
                    },
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Section 2: Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by company name, title, invoice...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("search_text_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            // Section 3: Horizontal Filters System (Folders)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Receipt Workspace Folders",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFolder == null,
                        onClick = { viewModel.selectFolder(null) },
                        label = { Text("All Folders") },
                        leadingIcon = { if (selectedFolder == null) Icon(Icons.Default.Check, null) }
                    )
                }
                items(AppConstants.FOLDERS) { folder ->
                    FilterChip(
                        selected = selectedFolder == folder,
                        onClick = { viewModel.selectFolder(folder) },
                        label = { Text(folder) },
                        leadingIcon = { if (selectedFolder == folder) Icon(Icons.Default.Check, null) }
                    )
                }
            }

            // Section 4: Popular Templates Shortcuts
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎨 Smart Templates",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val demoTemplates = listOf(
                    Triple("Business Invoice", "Glassmorphism", "Premium design for consultants & agencies"),
                    Triple("Restaurant Receipt", "iOS Style", "Polished client bills for food items"),
                    Triple("Freelancer Invoice", "Material You", "Custom layout with taxes/discounts"),
                    Triple("E-Commerce Order", "Nothing OS Style", "Minimalist white and clean typography"),
                    Triple("Grocery Bill", "Pure Black AMOLED", "Fitted for ink-saving dark printing")
                )
                items(demoTemplates) { (category, style, desc) ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .clickable {
                                viewModel.clearEditingItems()
                                // Prepopulate with default item to kickstart editor
                                viewModel.addEditingItem("Consulting Services", 1, 150.0)
                                onNavigateToEditor(null)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = style,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.outline),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Section 5: Current User Receipts Library
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "🧾 Saved Receipts Library (${filteredReceipts.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            if (filteredReceipts.isEmpty()) {
                // Polished Empty State as required by Frontend Design Guidelines
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FolderCopy,
                            contentDescription = "Empty Library",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Receipts Saved Here",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap the '+' action below or quick-create with template shortcuts to draft your first invoice.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredReceipts.forEach { receipt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPreview(receipt.id) }
                                .testTag("receipt_card_${receipt.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category Icon
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (receipt.category.contains("Invoice")) {
                                            Icons.Default.Receipt
                                        } else {
                                            Icons.Default.LocalMall
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = receipt.companyName.ifEmpty { "Receipt Studio X" },
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${receipt.category} • ${receipt.styleTemplate}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${receipt.currencyCode} ${"%.2f".format(receipt.total)}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        if (receipt.isDraft) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.errorContainer
                                            ) {
                                                Text(
                                                    text = "DRAFT",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Quick Actions
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(receipt) }
                                ) {
                                    Icon(
                                        imageVector = if (receipt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (receipt.isFavorite) Color.Red else MaterialTheme.colorScheme.outline
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteReceipt(receipt.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
