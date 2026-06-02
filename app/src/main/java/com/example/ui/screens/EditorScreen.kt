package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppConstants
import com.example.data.ReceiptItem
import com.example.ui.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: ReceiptViewModel,
    receiptId: Long?, // optional pre-load for edits (in our MVP we treat as create for simplicity)
    onNavigateBack: () -> Unit
) {
    val editingItems by viewModel.editingItems.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("New Receipt Plan") }
    var category by remember { mutableStateOf(AppConstants.TEMPLATE_CATEGORIES[0]) }
    var styleTemplate by remember { mutableStateOf(AppConstants.THEME_STYLES[0]) }
    var invoiceNumber by remember { mutableStateOf("INV-${System.currentTimeMillis() / 100000}") }
    var folder by remember { mutableStateOf(AppConstants.FOLDERS[0]) }

    var companyName by remember { mutableStateOf("") }
    var companyAddress by remember { mutableStateOf("") }
    var companyPhone by remember { mutableStateOf("") }
    var companyEmail by remember { mutableStateOf("") }

    var selectedCurrency by remember { mutableStateOf(AppConstants.CURRENCIES[2]) } // USD default
    var taxRate by remember { mutableStateOf("0.0") } // e.g., VAT% or GST%
    var discountRate by remember { mutableStateOf("0.0") } // e.g., 5%
    var serviceCharge by remember { mutableStateOf("0.0") }
    var deliveryFee by remember { mutableStateOf("0.0") }

    var watermarkText by remember { mutableStateOf("") }
    var signatureName by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    // Dropdown selectors states
    var categoryExpanded by remember { mutableStateOf(false) }
    var templateExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var folderExpanded by remember { mutableStateOf(false) }

    // Editing item inputs
    var newItemName by remember { mutableStateOf("") }
    var newItemQty by remember { mutableStateOf("1") }
    var newItemPrice by remember { mutableStateOf("") }

    // Calculations based on editingItems
    val subTotal = editingItems.sumOf { it.price * it.quantity }
    val discountVal = discountRate.toDoubleOrNull() ?: 0.0
    val taxVal = taxRate.toDoubleOrNull() ?: 0.0
    val serviceVal = serviceCharge.toDoubleOrNull() ?: 0.0
    val deliveryVal = deliveryFee.toDoubleOrNull() ?: 0.0

    val discountAmount = if (discountVal > 0) (subTotal * (discountVal / 100.0)) else 0.0
    val taxAmount = (subTotal - discountAmount) * (taxVal / 100.0)
    val grandTotal = subTotal - discountAmount + taxAmount + serviceVal + deliveryVal

    Scaffold(
        modifier = Modifier.testTag("editor_screen"),
        topBar = {
            MediumTopAppBar(
                title = { Text("Smart Studio X Designer", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (companyName.isEmpty()) {
                                companyName = "Studio Business"
                            }
                            viewModel.saveReceipt(
                                title = title,
                                category = category,
                                styleTemplate = styleTemplate,
                                companyName = companyName,
                                companyAddress = companyAddress,
                                companyPhone = companyPhone,
                                companyEmail = companyEmail,
                                currencyCode = selectedCurrency.code,
                                taxRate = taxVal,
                                discount = discountVal,
                                serviceCharge = serviceVal,
                                deliveryFee = deliveryVal,
                                isDraft = false,
                                folder = folder,
                                note = noteText,
                                watermark = watermarkText,
                                signatureName = signatureName,
                                invoiceNumber = invoiceNumber
                            )
                            onNavigateBack()
                        },
                        modifier = Modifier.testTag("save_receipt_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save & Export")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
            // Card 1: Document Settings
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "1. Document Metadata",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Template Group Label") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Row of drop downs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { categoryExpanded = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(category, maxLines = 1)
                            }
                            DropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                AppConstants.TEMPLATE_CATEGORIES.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Style Template Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { templateExpanded = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(styleTemplate, maxLines = 1)
                            }
                            DropdownMenu(
                                expanded = templateExpanded,
                                onDismissRequest = { templateExpanded = false }
                            ) {
                                AppConstants.THEME_STYLES.forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style) },
                                        onClick = {
                                            styleTemplate = style
                                            templateExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Invoice Number
                        OutlinedTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = { Text("Bill Number / Code") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Folder association Selector
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { folderExpanded = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(folder)
                            }
                            DropdownMenu(
                                expanded = folderExpanded,
                                onDismissRequest = { folderExpanded = false }
                            ) {
                                AppConstants.FOLDERS.forEach { f ->
                                    DropdownMenuItem(
                                        text = { Text(f) },
                                        onClick = {
                                            folder = f
                                            folderExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Company / Business Profile
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Issuer Business Coordinates",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company Name / Brand") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = companyAddress,
                        onValueChange = { companyAddress = it },
                        label = { Text("Contact Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = companyPhone,
                            onValueChange = { companyPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = companyEmail,
                            onValueChange = { companyEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // Card 3: Multi-Currency & Line Item Manager
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Interactive Items Listing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )

                        // Currency Selector button
                        Box {
                            Button(
                                onClick = { currencyExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            ) {
                                Text("${selectedCurrency.symbol} (${selectedCurrency.code})")
                            }
                            DropdownMenu(
                                expanded = currencyExpanded,
                                onDismissRequest = { currencyExpanded = false }
                            ) {
                                AppConstants.CURRENCIES.forEach { cur ->
                                    DropdownMenuItem(
                                        text = { Text("${cur.name} (${cur.symbol})") },
                                        onClick = {
                                            selectedCurrency = cur
                                            currencyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Existing items lists
                    if (editingItems.isEmpty()) {
                        Text(
                            text = "* Please add at least one line item to calculate.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            editingItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "${item.quantity} x ${selectedCurrency.symbol} ${"%.2f".format(item.price)}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Text(
                                        "${selectedCurrency.symbol} ${"%.2f".format(item.price * item.quantity)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeEditingItem(index) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Item",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Dynamic Section to Add item
                    Text("Add Line Item", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Item Title / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newItemQty,
                            onValueChange = { newItemQty = it },
                            label = { Text("Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = newItemPrice,
                            onValueChange = { newItemPrice = it },
                            label = { Text("Price Per Unit") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val priceVal = newItemPrice.toDoubleOrNull() ?: 0.0
                            val qtyVal = newItemQty.toIntOrNull() ?: 1
                            if (newItemName.isNotEmpty() && priceVal > 0) {
                                viewModel.addEditingItem(newItemName, qtyVal, priceVal)
                                newItemName = ""
                                newItemQty = "1"
                                newItemPrice = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.AddCircle, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add To List")
                    }
                }
            }

            // Card 4: Taxes, Discounts & Surcharges
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "4. Tax Rates, Discounts & Adjustments",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = taxRate,
                            onValueChange = { taxRate = it },
                            label = { Text("VAT / GST (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = discountRate,
                            onValueChange = { discountRate = it },
                            label = { Text("Discount (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = serviceCharge,
                            onValueChange = { serviceCharge = it },
                            label = { Text("Service Fee (${selectedCurrency.symbol})") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = deliveryFee,
                            onValueChange = { deliveryFee = it },
                            label = { Text("Shipping / Delivery Fee") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Card 5: Extras: Watermark & Signatures
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "5. Watermark, Signatures & Remarks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )

                    OutlinedTextField(
                        value = watermarkText,
                        onValueChange = { watermarkText = it },
                        label = { Text("Custom Security Watermark (e.g. PAID, DRAFT)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = signatureName,
                        onValueChange = { signatureName = it },
                        label = { Text("Signer Name (Digital Signature Support)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Additional Notes or Terms (Footer Remark)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Calculations Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                        Text("${selectedCurrency.symbol} ${"%.2f".format(subTotal)}", fontWeight = FontWeight.Bold)
                    }
                    if (discountVal > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Discount ($discountVal%)", style = MaterialTheme.typography.bodyMedium, color = Color.Green)
                            Text("- ${selectedCurrency.symbol} ${"%.2f".format(discountAmount)}", color = Color.Green)
                        }
                    }
                    if (taxVal > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Taxes ($taxVal%)", style = MaterialTheme.typography.bodyMedium)
                            Text("+ ${selectedCurrency.symbol} ${"%.2f".format(taxAmount)}")
                        }
                    }
                    if (serviceVal > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Service Fee", style = MaterialTheme.typography.bodyMedium)
                            Text("+ ${selectedCurrency.symbol} ${"%.2f".format(serviceVal)}")
                        }
                    }
                    if (deliveryVal > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                            Text("+ ${selectedCurrency.symbol} ${"%.2f".format(deliveryVal)}")
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GRAND TOTAL", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
                        Text(
                            "${selectedCurrency.symbol} ${"%.2f".format(grandTotal)}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
