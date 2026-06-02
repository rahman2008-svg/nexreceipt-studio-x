package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ReceiptJsonSerializer {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val listType = Types.newParameterizedType(List::class.java, ReceiptItem::class.java)
    private val adapter = moshi.adapter<List<ReceiptItem>>(listType)

    fun toJson(items: List<ReceiptItem>): String {
        return adapter.toJson(items) ?: "[]"
    }

    fun fromJson(json: String?): List<ReceiptItem> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class ReceiptRepository(private val receiptDao: ReceiptDao) {

    val allReceipts: Flow<List<Receipt>> = receiptDao.getAllReceipts()
    val activeProfile: Flow<UserProfile?> = receiptDao.getActiveProfile()

    suspend fun getReceiptById(id: Long): Receipt? {
        return receiptDao.getReceiptById(id)
    }

    suspend fun insertReceipt(receipt: Receipt): Long {
        return receiptDao.insertReceipt(receipt)
    }

    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }

    suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.deleteReceipt(receipt)
    }

    suspend fun deleteReceiptById(id: Long) {
        receiptDao.deleteReceiptById(id)
    }

    // Profile handling
    suspend fun loginAsUser(email: String, name: String, companyName: String? = null, role: String = "USER") {
        receiptDao.clearProfiles()
        val newProfile = UserProfile(
            email = email,
            name = name,
            companyName = companyName,
            role = role,
            isGuest = false
        )
        receiptDao.insertProfile(newProfile)
    }

    suspend fun loginAsGuest() {
        receiptDao.clearProfiles()
        val guestProfile = UserProfile(
            email = "guest@nexreceipt.com",
            name = "Guest User",
            companyName = "My Business",
            role = "USER",
            isGuest = true
        )
        receiptDao.insertProfile(guestProfile)
    }

    suspend fun updateProfile(profile: UserProfile) {
        receiptDao.insertProfile(profile)
    }

    suspend fun logout() {
        receiptDao.clearProfiles()
    }
}
