package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class BankRepository(private val db: AppDatabase) {

    private val beneficiaryDao = db.beneficiaryDao()
    private val transactionDao = db.transactionDao()

    // Query streams
    val beneficiaries: Flow<List<Beneficiary>> = beneficiaryDao.getAllFlow()
    val transactions: Flow<List<Transaction>> = transactionDao.getAllFlow()

    // Beneficiary actions
    suspend fun addBeneficiary(beneficiary: Beneficiary): Long {
        return beneficiaryDao.insert(beneficiary)
    }

    suspend fun updateBeneficiary(beneficiary: Beneficiary) {
        beneficiaryDao.update(beneficiary)
    }

    suspend fun deleteBeneficiary(beneficiary: Beneficiary) {
        beneficiaryDao.delete(beneficiary)
    }

    // Transaction & Transfer Actions
    suspend fun performTransfer(
        beneficiaryName: String,
        beneficiaryAccount: String,
        amount: Double,
        remarks: String
    ): String {
        // Generate a random Reference details
        val randomRef = "SBI" + (10000000000L + (Math.random() * 89999999999L).toLong()).toString()
        val desc = if (remarks.isNotBlank()) {
            "Trf to $beneficiaryName A/c $beneficiaryAccount Rmk: $remarks"
        } else {
            "Trf to $beneficiaryName A/c $beneficiaryAccount via IMPS"
        }

        val debitTrx = Transaction(
            timestamp = System.currentTimeMillis(),
            type = "DEBIT",
            description = desc,
            amount = amount,
            refNumber = randomRef,
            category = "Transfer"
        )
        transactionDao.insert(debitTrx)
        return randomRef
    }

    suspend fun clearAllTransactions() {
        transactionDao.deleteAll()
    }

    // Direct insert of external entries if needed (e.g., manual cash deposits/salary credited simulation)
    suspend fun addCreditTransaction(description: String, amount: Double, source: String): String {
        val randomRef = "SBI" + (10000000000L + (Math.random() * 89999999999L).toLong()).toString()
        val creditTrx = Transaction(
            timestamp = System.currentTimeMillis(),
            type = "CREDIT",
            description = description,
            amount = amount,
            refNumber = randomRef,
            category = source
        )
        transactionDao.insert(creditTrx)
        return randomRef
    }
}
