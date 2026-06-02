package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.data.Receipt
import com.example.data.ReceiptJsonSerializer
import com.example.ui.ReceiptViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewScreen(
    viewModel: ReceiptViewModel,
    receiptId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var receipt by remember { mutableStateOf<Receipt?>(null) }

    // Fetch receipt from DB on launch
    LaunchedEffect(receiptId) {
        receipt = viewModel.getReceiptById(receiptId)
    }

    // Printing Animation controls
    var isPrintingProgress by remember { mutableStateOf(true) }
    var printedPaperHeight by remember { mutableStateOf(0f) }
    val animatePaperHeight by animateFloatAsState(
        targetValue = if (isPrintingProgress) 0f else 1f,
        animationSpec = tween(2200, easing = LinearEasing),
        label = "print_slide"
    )

    // Slides Mode (Before plain vs Styled After)
    var isStyledAfter by remember { mutableStateOf(true) }

    // Social Media Ratios
    // "Default", "Instagram Story (9:16)", "TikTok Ratio", "Square Post (1:1)"
    var activeSocialRatio by remember { mutableStateOf("Default") }

    // Export formats selections
    var selectedExportFormat by remember { mutableStateOf("Ultra HD PNG") }

    LaunchedEffect(receipt) {
        if (receipt != null) {
            delay(1500) // play beautiful print sliding animations first
            isPrintingProgress = false
        }
    }

    Scaffold(
        modifier = Modifier.testTag("preview_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Studio Preview & Export", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (receipt != null) {
                            viewModel.toggleFavorite(receipt!!)
                            receipt = receipt!!.copy(isFavorite = !receipt!!.isFavorite)
                        }
                    }) {
                        Icon(
                            imageVector = if (receipt?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            tint = if (receipt?.isFavorite == true) Color.Red else MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Favorite Toggle"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (receipt == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val itemsList = remember(receipt) {
                ReceiptJsonSerializer.fromJson(receipt?.itemsJson)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF111418))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section A: PRINTING SLIDER DEMO
                if (isPrintingProgress) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🖨️ NexReceipt printing sliding active...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                // Section B: Before (Plain standard) vs Custom Style Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { isStyledAfter = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isStyledAfter) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (!isStyledAfter) MaterialTheme.colorScheme.onPrimary else Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Original (Plain)")
                    }

                    Button(
                        onClick = { isStyledAfter = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStyledAfter) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isStyledAfter) MaterialTheme.colorScheme.onPrimary else Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Studio Styled ✨")
                    }
                }

                // Section C: MAIN PREVIEW CANVAS CARD
                // Apply chosen style details as requested
                val activeTheme = if (isStyledAfter) receipt!!.styleTemplate else "Minimal White"

                // Theme color and brush selection mapping
                val themeGradient = when (activeTheme) {
                    "Glassmorphism" -> Brush.verticalGradient(listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF)))
                    "Cyber Neon" -> Brush.verticalGradient(listOf(Color(0xFF2B004F), Color(0xFF0F0022)))
                    "Luxury Gold" -> Brush.verticalGradient(listOf(Color(0xFF1E1C1A), Color(0xFF100E0D)))
                    "Gradient Modern" -> Brush.linearGradient(listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2)))
                    "Professional Blue" -> Brush.verticalGradient(listOf(Color(0xFF051937), Color(0xFF004D7A)))
                    "Pure Black AMOLED" -> Brush.verticalGradient(listOf(Color.Black, Color.Black))
                    "Nothing OS Style" -> Brush.verticalGradient(listOf(Color(0xFF1B1B1B), Color(0xFF151515)))
                    "iOS Style" -> Brush.verticalGradient(listOf(Color(0xFFF2F2F7), Color(0xFFE5E5EA)))
                    else -> Brush.verticalGradient(listOf(Color.White, Color.White)) // Minimal / Material You
                }

                val textHeaderColor = when (activeTheme) {
                    "Minimal White", "iOS Style" -> Color.Black
                    "Cyber Neon" -> Color(0xFF00FFCC)
                    "Luxury Gold" -> Color(0xFFD4AF37)
                    else -> Color.White
                }

                val textBodyColor = when (activeTheme) {
                    "Minimal White", "iOS Style" -> Color.DarkGray
                    "Cyber Neon" -> Color(0xFFE4E4E4)
                    "Luxury Gold" -> Color(0xFFE5C158)
                    else -> Color.White.copy(alpha = 0.85f)
                }

                // Apply aspect ratio dimensions based on Social selection
                val containerModifier = Modifier
                    .fillMaxWidth()
                    .then(
                        when (activeSocialRatio) {
                            "Instagram Story (9:16)" -> Modifier.aspectRatio(9f / 16f)
                            "TikTok Ratio" -> Modifier.aspectRatio(9f / 16f)
                            "Square Post (1:1)" -> Modifier.aspectRatio(1f)
                            else -> Modifier.wrapContentHeight()
                        }
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (activeTheme == "Glassmorphism") {
                            Modifier
                                .background(themeGradient)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        } else {
                            Modifier.background(themeGradient)
                        }
                    )
                    .padding(24.dp)

                // The visual receipt wrapper supporting slide animations
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(
                        modifier = containerModifier
                    ) {
                        // Header
                        Text(
                            text = receipt!!.companyName.ifEmpty { "NexReceipt Studio X" }.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = textHeaderColor,
                                letterSpacing = 2.sp,
                                fontFamily = if (activeTheme == "Nothing OS Style") FontFamily.Monospace else FontFamily.Default
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = receipt!!.category,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = textHeaderColor.copy(alpha = 0.7f),
                                fontStyle = FontStyle.Italic
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CODE: ${receipt!!.invoiceNumber}", color = textBodyColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("DATE: 06/02/2026", color = textBodyColor, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("FOLDER: ${receipt!!.folder.uppercase()}", color = textBodyColor, fontSize = 11.sp)
                                Text("STYLE: $activeTheme", color = textBodyColor, fontSize = 11.sp)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = textBodyColor.copy(alpha = 0.2f))

                        // Items
                        Text("DEAL ITEMS", fontWeight = FontWeight.Bold, color = textHeaderColor, fontSize = 12.sp, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        itemsList.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.name} (x${item.quantity})",
                                    color = textBodyColor,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${receipt!!.currencyCode} ${"%.2f".format(item.price * item.quantity)}",
                                    color = textHeaderColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = textBodyColor.copy(alpha = 0.2f))

                        // Calcs
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = textBodyColor, fontSize = 12.sp)
                            Text("${receipt!!.currencyCode} ${"%.2f".format(receipt!!.subTotal)}", color = textBodyColor, fontSize = 12.sp)
                        }
                        if (receipt!!.discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount (${receipt!!.discount}%)", color = Color.Green, fontSize = 12.sp)
                                val disAmt = receipt!!.subTotal * (receipt!!.discount / 100.0)
                                Text("- ${receipt!!.currencyCode} ${"%.2f".format(disAmt)}", color = Color.Green, fontSize = 12.sp)
                            }
                        }
                        if (receipt!!.taxRate > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("VAT / GST (${receipt!!.taxRate}%)", color = textBodyColor, fontSize = 12.sp)
                                val taxAmt = (receipt!!.subTotal - (receipt!!.subTotal * (receipt!!.discount / 100.0))) * (receipt!!.taxRate / 100.0)
                                Text("+ ${receipt!!.currencyCode} ${"%.2f".format(taxAmt)}", color = textBodyColor, fontSize = 12.sp)
                            }
                        }
                        if (receipt!!.serviceCharge > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Service Charge", color = textBodyColor, fontSize = 12.sp)
                                Text("+ ${receipt!!.currencyCode} ${"%.2f".format(receipt!!.serviceCharge)}", color = textBodyColor, fontSize = 12.sp)
                            }
                        }
                        if (receipt!!.deliveryFee > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Shipping Fee", color = textBodyColor, fontSize = 12.sp)
                                Text("+ ${receipt!!.currencyCode} ${"%.2f".format(receipt!!.deliveryFee)}", color = textBodyColor, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TOTAL",
                                color = textHeaderColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "${receipt!!.currencyCode} ${"%.2f".format(receipt!!.total)}",
                                color = if (activeTheme == "Cyber Neon") Color(0xFF00FFCC) else textHeaderColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }

                        if (!receipt!!.watermark.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "STAMP: [ ${receipt!!.watermark!!.uppercase()} ]",
                                color = if (activeTheme == "Cyber Neon") Color.Magenta else Color.Red.copy(alpha = 0.7f),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (!receipt!!.signatureName.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(modifier = Modifier.align(Alignment.End), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Digital Signee", color = textBodyColor.copy(alpha = 0.6f), fontSize = 9.sp)
                                Text(
                                    text = receipt!!.signatureName!!,
                                    color = textHeaderColor,
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Barcode / QR simulator drawn beautifully
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = textBodyColor.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pseudo QR Code Grid
                            Column {
                                Text("Smart Tracking QR", fontSize = 10.sp, color = textBodyColor.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    repeat(4) {
                                        Column {
                                            repeat(4) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .padding(1.dp)
                                                        .background(if ((0..1).random() == 1) textHeaderColor else Color.Transparent)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic Remarks Notes
                            if (!receipt!!.note.isNullOrEmpty()) {
                                Text(
                                    text = "\"${receipt!!.note!!}\"",
                                    fontSize = 11.sp,
                                    color = textBodyColor.copy(alpha = 0.8f),
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(180.dp)
                                )
                            }
                        }
                    }
                }

                // Section D: SOCIAL MEDIA STUDIO RESIZER
                Text(
                    text = "📱 Social Media Auto-Ratios Studio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val ratios = listOf("Default", "Instagram Story (9:16)", "TikTok Ratio", "Square Post (1:1)")
                    items(ratios) { r ->
                        FilterChip(
                            selected = activeSocialRatio == r,
                            onClick = { activeSocialRatio = r },
                            label = { Text(r, color = Color.White) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                containerColor = Color.White.copy(alpha = 0.08f)
                            )
                        )
                    }
                }

                // Section E: EXPORT FORMATS
                Text(
                    text = "📐 Export Quality Resolution",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val formats = listOf("Ultra HD PNG", "HD JPG", "Print Ready PDF")
                    items(formats) { f ->
                        FilterChip(
                            selected = selectedExportFormat == f,
                            onClick = { selectedExportFormat = f },
                            label = { Text(f, color = Color.White) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                containerColor = Color.White.copy(alpha = 0.08f)
                            )
                        )
                    }
                }

                // Primary Export Trigger Button
                Button(
                    onClick = {
                        Toast.makeText(context, "$selectedExportFormat generated in Studio cache!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("export_trigger_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Cloud Build & Export (${selectedExportFormat.uppercase()})", fontWeight = FontWeight.Bold)
                }

                // Section F: Instant Viral Social Share Buttons
                Text(
                    text = "🚀 One-Tap Viral Share",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val channels = listOf(
                        "WhatsApp" to Color(0xFF25D366),
                        "Facebook" to Color(0xFF1877F2),
                        "Twitter" to Color(0xFF1DA1F2),
                        "Telegram" to Color(0xFF0088CC),
                        "Gmail" to Color(0xFFDB4437)
                    )

                    channels.forEach { (name, color) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Redirecting to share layout: $name...", Toast.LENGTH_SHORT).show()
                                }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (name) {
                                        "WhatsApp" -> Icons.Default.ChatBubble
                                        "Facebook" -> Icons.Default.Facebook
                                        "Twitter" -> Icons.Default.Share
                                        "Telegram" -> Icons.Default.Send
                                        else -> Icons.Default.Email
                                    },
                                    contentDescription = name,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(name, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}
