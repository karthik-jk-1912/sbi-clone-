package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = "beneficiary")
data class Beneficiary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val accountNumber: String,
    val ifsc: String,
    val nickName: String,
    val limitAmount: Double,
    val addedOn: Long = System.currentTimeMillis()
)

@Entity(tableName = "bank_transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long,
    val type: String, // "DEBIT" or "CREDIT"
    val description: String,
    val amount: Double,
    val refNumber: String,
    val category: String = "Transfer" // "Salary", "Bills", "Shopping", "Transfer", etc.
)

@Dao
interface BeneficiaryDao {
    @Query("SELECT * FROM beneficiary ORDER BY name ASC")
    fun getAllFlow(): Flow<List<Beneficiary>>

    @Query("SELECT * FROM beneficiary")
    suspend fun getAll(): List<Beneficiary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(beneficiary: Beneficiary): Long

    @Update
    suspend fun update(beneficiary: Beneficiary)

    @Delete
    suspend fun delete(beneficiary: Beneficiary)

    @Query("SELECT * FROM beneficiary WHERE id = :id")
    suspend fun getById(id: Long): Beneficiary?
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM bank_transaction ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Query("DELETE FROM bank_transaction")
    suspend fun deleteAll()
}

@Database(entities = [Beneficiary::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun beneficiaryDao(): BeneficiaryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sbi_net_banking_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                
                // Trigger background prepopulate checks safely off main thread
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val beneficiaryDao = instance.beneficiaryDao()
                        if (beneficiaryDao.getAll().isEmpty()) {
                            prepopulateData(instance)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                instance
            }
        }

        private suspend fun prepopulateData(db: AppDatabase) {
            val beneficiaryDao = db.beneficiaryDao()
            val transactionDao = db.transactionDao()

            // Prepopulate some default realistic beneficiaries
            beneficiaryDao.insert(
                Beneficiary(
                    name = "Rohan Sharma",
                    accountNumber = "12345678901",
                    ifsc = "SBIN0000301",
                    nickName = "Rohan",
                    limitAmount = 150000.00
                )
            )
            beneficiaryDao.insert(
                Beneficiary(
                    name = "Priya Patel",
                    accountNumber = "98765432109",
                    ifsc = "ICIC0000021",
                    nickName = "Priya Sis",
                    limitAmount = 50000.00
                )
            )
            beneficiaryDao.insert(
                Beneficiary(
                    name = "Aarav Rawat",
                    accountNumber = "11223344556",
                    ifsc = "HDFC0000104",
                    nickName = "Aarav Business",
                    limitAmount = 250000.00
                )
            )

            // Let's seed initial transactions spanning the last 15 days
            val currentTime = System.currentTimeMillis()
            val hourInMillis = 3600000L
            val dayInMillis = 86400000L

            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 10 * dayInMillis,
                    type = "CREDIT",
                    description = "Opening Balance Deposit",
                    amount = 50000.00,
                    refNumber = "SBI98263539129",
                    category = "Deposit"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 8 * dayInMillis - 2 * hourInMillis,
                    type = "DEBIT",
                    description = "Electricity Bill Pay MSPDCL",
                    amount = 2450.00,
                    refNumber = "SBI19485720194",
                    category = "Bills"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 7 * dayInMillis,
                    type = "CREDIT",
                    description = "Monthly Salary Credited",
                    amount = 72000.00,
                    refNumber = "SBI49285710294",
                    category = "Salary"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 5 * dayInMillis - 3 * hourInMillis,
                    type = "DEBIT",
                    description = "ATM Cash Withdrawal - New Delhi",
                    amount = 10000.00,
                    refNumber = "SBI10394857211",
                    category = "Cash"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 4 * dayInMillis,
                    type = "DEBIT",
                    description = "Supermarket Grocery YonoUPI",
                    amount = 4580.00,
                    refNumber = "SBI39485710291",
                    category = "Shopping"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 3 * dayInMillis - 5 * hourInMillis,
                    type = "CREDIT",
                    description = "Cash Deposit Branch 01032",
                    amount = 15000.00,
                    refNumber = "SBI20394857291",
                    category = "Deposit"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 1 * dayInMillis,
                    type = "DEBIT",
                    description = "Transfer to Rohan Sharma via IMPS",
                    amount = 5000.00,
                    refNumber = "SBI10495829103",
                    category = "Transfer"
                )
            )
            transactionDao.insert(
                Transaction(
                    timestamp = currentTime - 10 * hourInMillis,
                    type = "DEBIT",
                    description = "Netflix Dining & Entertainment",
                    amount = 649.00,
                    refNumber = "SBI04928103928",
                    category = "Entertainment"
                )
            )
        }
    }
}
