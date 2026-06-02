package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY billingDate DESC")
    fun getAllReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceiptById(id: Long): Receipt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt): Long

    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: Long)

    // User Profile integration
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getActiveProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE email = :email LIMIT 1")
    suspend fun getProfileByEmail(email: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles")
    suspend fun clearProfiles()
}
