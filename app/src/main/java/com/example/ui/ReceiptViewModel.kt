package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReceiptViewModel(
    application: Application,
    private val repository: ReceiptRepository
) : AndroidViewModel(application) {

    // Reactive states
    val receipts: StateFlow<List<Receipt>> = repository.allReceipts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProfile: StateFlow<UserProfile?> = repository.activeProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin-specific mock user count & stats to simulate control center
    private val _adminMetrics = MutableStateFlow(AdminStats())
    val adminMetrics: StateFlow<AdminStats> = combine(receipts, _adminMetrics) { receiptList, stats ->
        val totalReceipts = receiptList.size
        val totalExports = totalReceipts * 2 + 15 // simulate dynamic reports
        val favoriteCount = receiptList.count { it.isFavorite }
        val draftCount = receiptList.count { it.isDraft }
        
        // Count category occurrences
        val categoryCounts = receiptList.groupBy { it.category }
            .mapValues { it.value.size }
        val popularCategory = categoryCounts.maxByOrNull { it.value }?.key ?: "Business Invoice"

        stats.copy(
            totalReceipts = totalReceipts,
            totalExports = totalExports,
            popularTemplate = popularCategory,
            draftReceipts = draftCount,
            favoriteReceipts = favoriteCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    // UI Search & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFolder = MutableStateFlow<String?>(null)
    val selectedFolder: StateFlow<String?> = _selectedFolder.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Filtered receipts
    val filteredReceipts: StateFlow<List<Receipt>> = combine(
        receipts, _searchQuery, _selectedFolder, _selectedCategory
    ) { list, query, folder, category ->
        list.filter { receipt ->
            val matchesQuery = query.isEmpty() || 
                receipt.title.contains(query, ignoreCase = true) ||
                receipt.companyName.contains(query, ignoreCase = true) ||
                receipt.invoiceNumber.contains(query, ignoreCase = true)
            
            val matchesFolder = folder == null || receipt.folder == folder
            val matchesCategory = category == null || receipt.category == category

            matchesQuery && matchesFolder && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Receipt state being edited/created
    private val _editingItems = MutableStateFlow<List<ReceiptItem>>(emptyList())
    val editingItems: StateFlow<List<ReceiptItem>> = _editingItems.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFolder(folder: String?) {
        _selectedFolder.value = folder
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    // Receipt CRUD
    fun saveReceipt(
        title: String,
        category: String,
        styleTemplate: String,
        companyName: String,
        companyAddress: String,
        companyPhone: String,
        companyEmail: String,
        currencyCode: String,
        taxRate: Double,
        discount: Double,
        serviceCharge: Double,
        deliveryFee: Double,
        isDraft: Boolean = false,
        folder: String = "Personal",
        note: String? = null,
        watermark: String? = null,
        signatureName: String? = null,
        invoiceNumber: String = ""
    ) {
        viewModelScope.launch {
            val items = _editingItems.value
            val subTotal = items.sumOf { it.price * it.quantity }
            
            // Auto Calculation logic
            val discountAmount = if (discount > 0) (subTotal * (discount / 100.0)) else 0.0
            val taxAmount = (subTotal - discountAmount) * (taxRate / 100.0)
            val total = subTotal - discountAmount + taxAmount + serviceCharge + deliveryFee

            val qrCodeData = "Receipt ID: ${System.currentTimeMillis()}\nCompany: $companyName\nTotal: $currencyCode ${"%.2f".format(total)}"

            val receipt = Receipt(
                title = title,
                category = category,
                styleTemplate = styleTemplate,
                companyName = companyName,
                companyAddress = companyAddress,
                companyPhone = companyPhone,
                companyEmail = companyEmail,
                itemsJson = ReceiptJsonSerializer.toJson(items),
                subTotal = subTotal,
                taxRate = taxRate,
                discount = discount,
                serviceCharge = serviceCharge,
                deliveryFee = deliveryFee,
                total = total,
                currencyCode = currencyCode,
                qrCodeContent = qrCodeData,
                isDraft = isDraft,
                folder = folder,
                note = note,
                watermark = watermark,
                signatureName = signatureName,
                invoiceNumber = invoiceNumber.ifEmpty { "INV-${System.currentTimeMillis() / 1000}" }
            )
            repository.insertReceipt(receipt)
            _editingItems.value = emptyList() // clear draft items on successful save
        }
    }

    fun updateReceipt(receipt: Receipt) {
        viewModelScope.launch {
            repository.updateReceipt(receipt)
        }
    }

    fun toggleFavorite(receipt: Receipt) {
        viewModelScope.launch {
            repository.updateReceipt(receipt.copy(isFavorite = !receipt.isFavorite))
        }
    }

    fun deleteReceipt(id: Long) {
        viewModelScope.launch {
            repository.deleteReceiptById(id)
        }
    }

    suspend fun getReceiptById(id: Long): Receipt? {
        return repository.getReceiptById(id)
    }

    // Receipt editing items manager
    fun setEditingItems(items: List<ReceiptItem>) {
        _editingItems.value = items
    }

    fun addEditingItem(name: String, quantity: Int, price: Double) {
        val updated = _editingItems.value.toMutableList()
        updated.add(ReceiptItem(name, quantity, price))
        _editingItems.value = updated
    }

    fun removeEditingItem(index: Int) {
        val updated = _editingItems.value.toMutableList()
        if (index in updated.indices) {
            updated.removeAt(index)
            _editingItems.value = updated
        }
    }

    fun clearEditingItems() {
        _editingItems.value = emptyList()
    }

    // Authentication session state management
    fun login(email: String, name: String, companyName: String? = null, isAdmin: Boolean = false) {
        viewModelScope.launch {
            val role = if (isAdmin || email.contains("admin") || email == "princearabdurrahman57@gmail.com") "SUPER_ADMIN" else "USER"
            repository.loginAsUser(email, name, companyName, role)
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            repository.loginAsGuest()
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

data class AdminStats(
    val totalUsers: Int = 124,
    val activeUsers: Int = 89,
    val totalReceipts: Int = 0,
    val totalExports: Int = 0,
    val popularTemplate: String = "Business Invoice",
    val draftReceipts: Int = 0,
    val favoriteReceipts: Int = 0
)

class ReceiptViewModelFactory(
    private val application: Application,
    private val repository: ReceiptRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceiptViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
