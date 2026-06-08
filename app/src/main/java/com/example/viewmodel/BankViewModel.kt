package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

// Sealed Screen destination class for Navigation
sealed interface Screen {
    object Login : Screen
    object MainDashboard : Screen
    object BeneficiariesList : Screen
    data class AddEditBeneficiary(val beneficiary: Beneficiary? = null) : Screen
    data class FundTransfer(val preselectedBeneficiary: Beneficiary? = null) : Screen
    object TransactionHistory : Screen
}

class BankViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BankRepository(db)

    // Backstack for custom screen navigation
    private val _screenStack = mutableStateListOf<Screen>(Screen.Login)
    val currentScreen: Screen get() = _screenStack.lastOrNull() ?: Screen.Login

    fun navigateTo(screen: Screen) {
        _screenStack.add(screen)
    }

    fun navigateBack() {
        if (_screenStack.size > 1) {
            _screenStack.removeAt(_screenStack.lastIndex)
        }
    }

    // --- State Streams ---
    val beneficiaries: StateFlow<List<Beneficiary>> = repository.beneficiaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Dynamic Balance State ---
    // Start with a base balance, then calculate dynamically from the transactions flow
    private val baseBalance = 95000.00
    val currentBalance: StateFlow<Double> = repository.transactions
        .map { txList ->
            val credits = txList.filter { it.type == "CREDIT" }.sumOf { it.amount }
            val debits = txList.filter { it.type == "DEBIT" }.sumOf { it.amount }
            baseBalance + credits - debits
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), baseBalance)

    // --- Login form states ---
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var captchaInput by mutableStateOf("")
    var realCaptcha by mutableStateOf("")
    var isLoggedIn by mutableStateOf(false)
    var loginError by mutableStateOf("")
    var simulatedOtpSent by mutableStateOf(false)
    var otpInput by mutableStateOf("")
    var otpError by mutableStateOf("")

    // User profile state
    val accountNo = "30091873612"
    val branchName = "PARLIAMENT STREET BD, NEW DELHI"
    val ifscCode = "SBIN0000691"
    val accountType = "Savings Account"

    // --- Beneficiary form states ---
    var benefName by mutableStateOf("")
    var benefAccountNo by mutableStateOf("")
    var benefConfirmAccountNo by mutableStateOf("")
    var benefIfsc by mutableStateOf("")
    var benefNickName by mutableStateOf("")
    var benefLimitAmount by mutableStateOf("100000")
    
    // Beneficiary form error messages
    var bNameError by mutableStateOf("")
    var bAccountNoError by mutableStateOf("")
    var bConfirmAccountNoError by mutableStateOf("")
    var bIfscError by mutableStateOf("")
    var bLimitError by mutableStateOf("")

    // --- Transfer forms states ---
    var transferAmount by mutableStateOf("")
    var transferRemarks by mutableStateOf("")
    var transferType by mutableStateOf("IMPS") // IMPS, NEFT, RTGS
    var transferStatusMsg by mutableStateOf<String?>(null)
    var lastTransferRef by mutableStateOf("")
    var transferShowOtpDialog by mutableStateOf(false)
    var transferOtpInput by mutableStateOf("")
    var transferOtpError by mutableStateOf("")
    var transferSelectedBeneficiary by mutableStateOf<Beneficiary?>(null)

    // --- Transaction History Filtering States ---
    var txSearchQuery by mutableStateOf("")
    var txSortBy by mutableStateOf("DATE_DESC") // DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
    var txFilterByCategory by mutableStateOf("ALL") // ALL, Transfer, Salary, Bills, Shopping, Cash

    init {
        regenerateCaptcha()
    }

    // --- Authentication Actions ---
    fun regenerateCaptcha() {
        val chars = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
        realCaptcha = (1..5)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
        captchaInput = ""
    }

    fun submitLogin() {
        loginError = ""
        otpError = ""
        
        if (username.isBlank() || password.isBlank()) {
            loginError = "Username and password cannot be empty."
            return
        }
        if (captchaInput.lowercase() != realCaptcha.lowercase()) {
            loginError = "Invalid Captcha code. Please try again."
            regenerateCaptcha()
            return
        }

        // Simulating highly secure login: Send dynamic OTP
        simulatedOtpSent = true
    }

    fun verifyLoginOtp() {
        if (otpInput == "123456" || otpInput == "sbi123" || otpInput.length == 6) {
            isLoggedIn = true
            simulatedOtpSent = false
            otpInput = ""
            _screenStack.clear()
            navigateTo(Screen.MainDashboard)
        } else {
            otpError = "Invalid OTP. For demo purposes use any 6-digit PIN (e.g. 123456)."
        }
    }

    fun performLogout() {
        isLoggedIn = false
        username = ""
        password = ""
        captchaInput = ""
        simulatedOtpSent = false
        otpInput = ""
        loginError = ""
        regenerateCaptcha()
        _screenStack.clear()
        navigateTo(Screen.Login)
    }

    // --- Beneficiary Actions ---
    fun setBeneficiaryFormForEditing(beneficiary: Beneficiary) {
        benefName = beneficiary.name
        benefAccountNo = beneficiary.accountNumber
        benefConfirmAccountNo = beneficiary.accountNumber
        benefIfsc = beneficiary.ifsc
        benefNickName = beneficiary.nickName
        benefLimitAmount = beneficiary.limitAmount.toInt().toString()
        clearBeneficiaryErrors()
    }

    fun clearBeneficiaryForm() {
        benefName = ""
        benefAccountNo = ""
        benefConfirmAccountNo = ""
        benefIfsc = ""
        benefNickName = ""
        benefLimitAmount = "100000"
        clearBeneficiaryErrors()
    }

    private fun clearBeneficiaryErrors() {
        bNameError = ""
        bAccountNoError = ""
        bConfirmAccountNoError = ""
        bIfscError = ""
        bLimitError = ""
    }

    fun saveBeneficiary(existingId: Long?): Boolean {
        clearBeneficiaryErrors()
        var hasError = false

        if (benefName.isBlank()) {
            bNameError = "Name is required."
            hasError = true
        }
        if (benefAccountNo.length < 9 || benefAccountNo.length > 18) {
            bAccountNoError = "Account number must be 9-18 digits."
            hasError = true
        }
        if (benefConfirmAccountNo != benefAccountNo) {
            bConfirmAccountNoError = "Account numbers do not match."
            hasError = true
        }
        
        // IFSC standard verification (11 chars, first 4 letters, 5th is 0, rest are branch digits/letters)
        val ifscRegex = Regex("^[A-Z]{4}0[A-Z0-9]{6}$")
        if (!ifscRegex.matches(benefIfsc.trim().uppercase())) {
            bIfscError = "Invalid IFSC format. Expected SBIN0000123 style (11 alphanumeric characters)."
            hasError = true
        }

        val limitVal = benefLimitAmount.toDoubleOrNull()
        if (limitVal == null || limitVal <= 0) {
            bLimitError = "Enter a valid maximum transfer limit."
            hasError = true
        }

        if (hasError) return false

        viewModelScope.launch {
            val finalNickname = if (benefNickName.isNotBlank()) benefNickName else benefName.split(" ").first()
            if (existingId != null && existingId != 0L) {
                // Update
                val updated = Beneficiary(
                    id = existingId,
                    name = benefName.trim(),
                    accountNumber = benefAccountNo.trim(),
                    ifsc = benefIfsc.trim().uppercase(),
                    nickName = finalNickname.trim(),
                    limitAmount = limitVal ?: 100000.0
                )
                repository.updateBeneficiary(updated)
            } else {
                // Add new
                val newBenef = Beneficiary(
                    name = benefName.trim(),
                    accountNumber = benefAccountNo.trim(),
                    ifsc = benefIfsc.trim().uppercase(),
                    nickName = finalNickname.trim(),
                    limitAmount = limitVal ?: 100000.0
                )
                repository.addBeneficiary(newBenef)
            }
        }
        return true
    }

    fun deleteBeneficiaryItem(beneficiary: Beneficiary) {
        viewModelScope.launch {
            repository.deleteBeneficiary(beneficiary)
        }
    }

    // --- Fund Transfer / Payment Actions ---
    fun clearTransferForm() {
        transferAmount = ""
        transferRemarks = ""
        transferType = "IMPS"
        transferStatusMsg = null
        lastTransferRef = ""
        transferShowOtpDialog = false
        transferOtpInput = ""
        transferOtpError = ""
    }

    fun submitTransfer() {
        val selected = transferSelectedBeneficiary ?: return
        val amt = transferAmount.toDoubleOrNull()
        if (amt == null || amt <= 0) {
            transferStatusMsg = "Please enter a valid, positive transfer amount."
            return
        }

        if (amt > currentBalance.value) {
            transferStatusMsg = "Insuffient balance. Available: ₹${String.format("%,.2f", currentBalance.value)}"
            return
        }

        if (amt > selected.limitAmount) {
            transferStatusMsg = "Transfer amount exceeds the daily set limit of ₹${String.format("%,.2f", selected.limitAmount)} for this beneficiary."
            return
        }

        // Prompt for transaction OTP
        transferOtpError = ""
        transferOtpInput = ""
        transferShowOtpDialog = true
    }

    fun confirmTransferWithOtp() {
        val selected = transferSelectedBeneficiary ?: return
        val amt = transferAmount.toDoubleOrNull() ?: return

        if (transferOtpInput == "123456" || transferOtpInput == "sbi123" || transferOtpInput.length == 6) {
            transferShowOtpDialog = false
            viewModelScope.launch {
                val ref = repository.performTransfer(
                    beneficiaryName = selected.name,
                    beneficiaryAccount = selected.accountNumber,
                    amount = amt,
                    remarks = transferRemarks
                )
                lastTransferRef = ref
                transferStatusMsg = "SUCCESS"
            }
        } else {
            transferOtpError = "Invalid transaction OTP PIN."
        }
    }

    // Simulate direct account operations like cash deposits (credits) to keep app active
    fun simulateCredit(amount: Double, description: String, category: String) {
        viewModelScope.launch {
            repository.addCreditTransaction(description, amount, category)
        }
    }
}
