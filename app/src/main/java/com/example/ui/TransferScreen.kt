package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Beneficiary
import com.example.ui.theme.*
import com.example.viewmodel.BankViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(viewModel: BankViewModel, modifier: Modifier = Modifier) {
    val beneficiaries by viewModel.beneficiaries.collectAsStateWithLifecycle()
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()

    var showBeneficiaryDropdown by remember { mutableStateOf(false) }

    // Init preselected values if any
    LaunchedEffect(Unit) {
        if (viewModel.transferSelectedBeneficiary == null && beneficiaries.isNotEmpty()) {
            viewModel.transferSelectedBeneficiary = beneficiaries.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fund Transfer Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("transfer_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SbiPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Receipts Success State representation
                if (viewModel.transferStatusMsg == "SUCCESS") {
                    TransferSuccessReceipt(
                        viewModel = viewModel,
                        onClose = {
                            viewModel.clearTransferForm()
                            viewModel.navigateBack()
                        }
                    )
                } else {
                    // MAIN TRANSFER WRITING AREA
                    
                    // Selected Account details banner
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = SbiSurfaceLight),
                        border = BorderStroke(1.dp, SbiBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SbiPrimary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Home, contentDescription = "Wallet", tint = SbiPrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Debiting Account:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SbiTextSecondary)
                                )
                                Text(
                                    text = "Savings A/c ******123 (Available: ₹${String.format("%,.2f", currentBalance)})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SbiTextDark)
                                )
                            }
                        }
                    }

                    // Selected Beneficiary Section
                    Text(
                        text = "1. CHOOSE BENEFICIARY RECIPIENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SbiTextSecondary,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBeneficiaryDropdown = true },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SbiPrimary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val selected = viewModel.transferSelectedBeneficiary
                                if (selected != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(SbiPrimary.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selected.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SbiPrimary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = selected.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SbiTextDark)
                                            )
                                            Text(
                                                text = "A/c: ${selected.accountNumber} | IFSC: ${selected.ifsc}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = SbiTextSecondary)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Select Beneficiary Recipient",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = SbiTextSecondary)
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown indicators", tint = SbiPrimary)
                            }
                        }

                        DropdownMenu(
                            expanded = showBeneficiaryDropdown,
                            onDismissRequest = { showBeneficiaryDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.surface)
                        ) {
                            if (beneficiaries.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No active beneficiaries - add one first") },
                                    onClick = { showBeneficiaryDropdown = false }
                                )
                            } else {
                                beneficiaries.forEach { benef ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(benef.name, fontWeight = FontWeight.Bold, color = SbiTextDark)
                                                Text("A/c: ${benef.accountNumber} | IFSC: ${benef.ifsc}", fontSize = 11.sp, color = SbiTextSecondary)
                                            }
                                        },
                                        onClick = {
                                            viewModel.transferSelectedBeneficiary = benef
                                            showBeneficiaryDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Payment Amount Input section
                    Text(
                        text = "2. ROUTING CHANNELS & AMOUNT PARAMETERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SbiTextSecondary,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SbiBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Transfer network selector chips
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("IMPS", "NEFT", "RTGS").forEach { option ->
                                    val isSelected = viewModel.transferType == option
                                    val bg = if (isSelected) SbiPrimary else SbiPrimary.copy(alpha = 0.08f)
                                    val tc = if (isSelected) Color.White else SbiPrimary

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(bg, RoundedCornerShape(8.dp))
                                            .clickable { viewModel.transferType = option }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = option,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = tc
                                                )
                                            )
                                            val speedStr = when (option) {
                                                "IMPS" -> "Instant"
                                                "NEFT" -> "2-4 hours"
                                                else -> "Direct IM"
                                            }
                                            Text(
                                                text = speedStr,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else SbiTextSecondary
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Amount TextField
                            OutlinedTextField(
                                value = viewModel.transferAmount,
                                onValueChange = { viewModel.transferAmount = it },
                                label = { Text("Transfer Amount (INR)") },
                                placeholder = { Text("Enter payment volume in INR") },
                                leadingIcon = {
                                    Text(
                                        "₹",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(color = SbiPrimary)
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("transfer_amount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Remarks TextField
                            OutlinedTextField(
                                value = viewModel.transferRemarks,
                                onValueChange = { viewModel.transferRemarks = it },
                                label = { Text("Remarks / Narrative (Optional)") },
                                placeholder = { Text("e.g. Rent, Gift, Business") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("transfer_remarks_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                )
                            )
                        }
                    }

                    // Alert presentation if any internal parameters fail (e.g. limit bounds/balance)
                    if (viewModel.transferStatusMsg != null && viewModel.transferStatusMsg != "SUCCESS") {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Error detail", tint = Color.Red)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = viewModel.transferStatusMsg ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }

                    // Submit Action Trigger Button
                    Button(
                        onClick = { viewModel.submitTransfer() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("transfer_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "AUTHORIZE TRANSFER",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }

        // Transaction Authorization OTP Dialog confirmation
        if (viewModel.transferShowOtpDialog) {
            Dialog(
                onDismissRequest = { viewModel.transferShowOtpDialog = false },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SbiPrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(SbiAccentOrange.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Authorize Lock icon",
                                tint = SbiAccentOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Authorize Transaction OTP PIN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiPrimary,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val benefName = viewModel.transferSelectedBeneficiary?.name ?: "Recipient"
                        Text(
                            text = "A dynamic IMPS OTP transaction authorization cipher was dispatched to register phone coordinates to authorize transferring ₹${viewModel.transferAmount} to $benefName.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SbiTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = viewModel.transferOtpInput,
                            onValueChange = { viewModel.transferOtpInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transfer_otp_input"),
                            label = { Text("6-Digit OTP Secure PIN") },
                            placeholder = { Text("Demo PIN is anything (e.g., 123456)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SbiPrimary,
                                focusedLabelColor = SbiPrimary
                            )
                        )

                        if (viewModel.transferOtpError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.transferOtpError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.transferShowOtpDialog = false
                                    viewModel.transferOtpInput = ""
                                }
                            ) {
                                Text("CANCEL", color = SbiTextSecondary)
                            }

                            Button(
                                onClick = { viewModel.confirmTransferWithOtp() },
                                colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary)
                            ) {
                                Text("CONFIRM FUNDS TRF")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Spectacular visual transfer success receipt showing full parameter confirmation details!
@Composable
fun TransferSuccessReceipt(viewModel: BankViewModel, onClose: () -> Unit) {
    val selected = viewModel.transferSelectedBeneficiary
    val amt = viewModel.transferAmount
    val refNum = viewModel.lastTransferRef
    val remarks = viewModel.transferRemarks.ifBlank { "Immediate Personal Payment via IMPS" }
    
    val dateStr = remember {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        formatter.format(Date())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, Color(0xFF008D6B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success circular bullet
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success tick icon",
                    tint = Color(0xFF008D6B),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TRANSACTION SUCCESSFUL",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF008D6B),
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "Funds debited and credited immediately",
                style = MaterialTheme.typography.bodySmall.copy(color = SbiTextSecondary)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = SbiBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Parameter details list grid
            ReceiptParameterRow("SBI UTR / Ref Number", refNum, selectFontMonospace = true)
            ReceiptParameterRow("Value Authorized", "₹$amt.00", isMajorSumColor = true)
            ReceiptParameterRow("Debit Account No", "Savings ******123")
            ReceiptParameterRow("Beneficiary Name", selected?.name ?: "Direct")
            ReceiptParameterRow("Beneficiary Account", selected?.accountNumber ?: "")
            ReceiptParameterRow("Beneficiary IFSC", selected?.ifsc ?: "")
            ReceiptParameterRow("Transfer System", viewModel.transferType)
            ReceiptParameterRow("Date / Timestamp", dateStr)
            ReceiptParameterRow("Narrative / Remarks", remarks)

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = SbiBorder)
            Spacer(modifier = Modifier.height(20.dp))

            // Download receipt banner informational trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SbiPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Finished", tint = SbiPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Receipt generated and stored to device.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SbiPrimary, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "GO BACK TO DASHBOARD",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun ReceiptParameterRow(
    label: String,
    value: String,
    selectFontMonospace: Boolean = false,
    isMajorSumColor: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = SbiTextSecondary, fontSize = 11.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isMajorSumColor) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isMajorSumColor) Color(0xFF008D6B) else SbiTextDark,
                fontFamily = if (selectFontMonospace) androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Default,
                fontSize = if (isMajorSumColor) 15.sp else 12.sp,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}
