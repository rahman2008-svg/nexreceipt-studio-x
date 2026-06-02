package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReceiptItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // e.g. "Business Invoice", "Restaurant Receipt", "E-Commerce Order", etc.
    val styleTemplate: String, // e.g. "Glassmorphism", "Material You", "Minimal White", etc.
    val companyName: String,
    val companyAddress: String = "",
    val companyPhone: String = "",
    val companyEmail: String = "",
    val billingDate: Long = System.currentTimeMillis(),
    val itemsJson: String, // List<ReceiptItem> serialized
    val subTotal: Double,
    val taxRate: Double = 0.0, // e.g. VAT or GST
    val discount: Double = 0.0,
    val serviceCharge: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double,
    val currencyCode: String = "USD", // e.g. "BDT", "INR", "USD", etc.
    val qrCodeContent: String = "",
    val isFavorite: Boolean = false,
    val folder: String = "Personal", // "Business", "Personal", "Client Work", "Store Bills"
    val isDraft: Boolean = false,
    val note: String? = null,
    val watermark: String? = null,
    val signatureName: String? = null,
    val signatureImageUri: String? = null,
    val invoiceNumber: String = ""
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val email: String,
    val name: String,
    val companyName: String? = null,
    val profileImage: String? = null,
    val role: String = "USER", // "USER" or "SUPER_ADMIN"
    val isGuest: Boolean = false,
    val address: String = "",
    val phone: String = ""
)

object AppConstants {
    val TEMPLATE_CATEGORIES = listOf(
        "Business Invoice",
        "Restaurant Receipt",
        "E-Commerce Order",
        "Grocery Bill",
        "Pharmacy Receipt",
        "Delivery Receipt",
        "Freelancer Invoice",
        "Subscription Receipt",
        "Gaming Purchase",
        "Event Ticket Receipt",
        "Hotel Bill",
        "Travel Invoice"
    )

    val THEME_STYLES = listOf(
        "Glassmorphism",
        "Material You",
        "Minimal White",
        "Pure Black AMOLED",
        "Cyber Neon",
        "Luxury Gold",
        "Nothing OS Style",
        "iOS Style",
        "Gradient Modern",
        "Professional Blue"
    )

    val FOLDERS = listOf(
        "Business",
        "Personal",
        "Client Work",
        "Store Bills"
    )

    data class CurrencyInfo(val code: String, val symbol: String, val name: String)

    val CURRENCIES = listOf(
        CurrencyInfo("BDT", "৳", "Bangladeshi Taka"),
        CurrencyInfo("INR", "₹", "Indian Rupee"),
        CurrencyInfo("USD", "$", "US Dollar"),
        CurrencyInfo("EUR", "€", "Euro"),
        CurrencyInfo("GBP", "£", "British Pound"),
        CurrencyInfo("JPY", "¥", "Japanese Yen"),
        CurrencyInfo("SAR", "﷼", "Saudi Riyal"),
        CurrencyInfo("AED", "د.إ", "UAE Dirham"),
        CurrencyInfo("MYR", "RM", "Malaysian Ringgit"),
        CurrencyInfo("SGD", "S$", "Singapore Dollar"),
        CurrencyInfo("CAD", "C$", "Canadian Dollar"),
        CurrencyInfo("AUD", "A$", "Australian Dollar")
    )
}
