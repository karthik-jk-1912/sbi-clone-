package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Beneficiary
import com.example.ui.theme.*
import com.example.viewmodel.BankViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiaryScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier,
    editBeneficiary: Beneficiary? = null
) {
    val beneficiaries by viewModel.beneficiaries.collectAsStateWithLifecycle()
    
    // Switch between LIST mode and ADD/EDIT mode
    var isFormMode by remember { mutableStateOf(editBeneficiary != null) }
    var currentEditingId by remember { mutableStateOf<Long?>(editBeneficiary?.id) }

    // Deletion Modal tracker
    var beneficiaryToDelete by remember { mutableStateOf<Beneficiary?>(null) }

    // Init form fields if editBeneficiary changes
    LaunchedEffect(editBeneficiary) {
        if (editBeneficiary != null) {
            viewModel.setBeneficiaryFormForEditing(editBeneficiary)
            currentEditingId = editBeneficiary.id
            isFormMode = true
        } else {
            viewModel.clearBeneficiaryForm()
            currentEditingId = null
            isFormMode = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isFormMode) {
                            if (currentEditingId != null) "Edit Beneficiary" else "Add Beneficiary"
                        } else {
                            "Manage Beneficiaries"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isFormMode) {
                                isFormMode = false
                                viewModel.clearBeneficiaryForm()
                                currentEditingId = null
                            } else {
                                viewModel.navigateBack()
                            }
                        },
                        modifier = Modifier.testTag("beneficiary_back_button")
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
        floatingActionButton = {
            if (!isFormMode) {
                FloatingActionButton(
                    onClick = {
                        viewModel.clearBeneficiaryForm()
                        currentEditingId = null
                        isFormMode = true
                    },
                    containerColor = SbiPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_benef_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add New Beneficiary")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (isFormMode) {
                // ADD / EDIT FORM LAYOUT
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, SbiBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header description
                            Text(
                                text = "BENEFICIARY PARAMETER DECLARATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SbiPrimary,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Beneficiary Name field
                            OutlinedTextField(
                                value = viewModel.benefName,
                                onValueChange = { viewModel.benefName = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("Enter beneficiary name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                ),
                                isError = viewModel.bNameError.isNotBlank()
                            )
                            if (viewModel.bNameError.isNotBlank()) {
                                Text(
                                    text = viewModel.bNameError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Account Number field
                            OutlinedTextField(
                                value = viewModel.benefAccountNo,
                                onValueChange = { viewModel.benefAccountNo = it },
                                label = { Text("Account Number") },
                                placeholder = { Text("Enter 9-18 digit account number") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_acc_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                ),
                                isError = viewModel.bAccountNoError.isNotBlank()
                            )
                            if (viewModel.bAccountNoError.isNotBlank()) {
                                Text(
                                    text = viewModel.bAccountNoError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Account Number field
                            OutlinedTextField(
                                value = viewModel.benefConfirmAccountNo,
                                onValueChange = { viewModel.benefConfirmAccountNo = it },
                                label = { Text("Confirm Account Number") },
                                placeholder = { Text("Re-enter account number") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_confirm_acc_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                ),
                                isError = viewModel.bConfirmAccountNoError.isNotBlank()
                            )
                            if (viewModel.bConfirmAccountNoError.isNotBlank()) {
                                Text(
                                    text = viewModel.bConfirmAccountNoError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // IFSC Code field
                            OutlinedTextField(
                                value = viewModel.benefIfsc,
                                onValueChange = { viewModel.benefIfsc = it },
                                label = { Text("IFSC Code") },
                                placeholder = { Text("e.g. SBIN0000301") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_ifsc_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                ),
                                isError = viewModel.bIfscError.isNotBlank()
                            )
                            if (viewModel.bIfscError.isNotBlank()) {
                                Text(
                                    text = viewModel.bIfscError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Nickname field
                            OutlinedTextField(
                                value = viewModel.benefNickName,
                                onValueChange = { viewModel.benefNickName = it },
                                label = { Text("Nickname (Optional)") },
                                placeholder = { Text("Will default to first name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_nick_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Transfer Limit field
                            OutlinedTextField(
                                value = viewModel.benefLimitAmount,
                                onValueChange = { viewModel.benefLimitAmount = it },
                                label = { Text("Daily Transfer Limit (₹)") },
                                placeholder = { Text("e.g. 100000") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("benef_limit_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SbiPrimary,
                                    focusedLabelColor = SbiPrimary
                                ),
                                isError = viewModel.bLimitError.isNotBlank()
                            )
                            if (viewModel.bLimitError.isNotBlank()) {
                                Text(
                                    text = viewModel.bLimitError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isFormMode = false
                                viewModel.clearBeneficiaryForm()
                                currentEditingId = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SbiPrimary)
                        ) {
                            Text("CANCEL", color = SbiPrimary)
                        }

                        Button(
                            onClick = {
                                if (viewModel.saveBeneficiary(currentEditingId)) {
                                    isFormMode = false
                                    viewModel.clearBeneficiaryForm()
                                    currentEditingId = null
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("benef_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SAVE DETAILS", color = Color.White)
                        }
                    }
                }
            } else {
                // LIST VIEW LAYOUT
                if (beneficiaries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Empty beneficiaries list",
                                tint = SbiTextSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Beneficiaries Added",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SbiTextDark
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create transfer networks by clicking the + button below to add bank accounts.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SbiTextSecondary),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Text(
                                text = "ACTIVE BENEFICIARY REGISTER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SbiTextSecondary,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        items(beneficiaries) { item ->
                            BeneficiaryRow(
                                beneficiary = item,
                                onEdit = {
                                    currentEditingId = item.id
                                    viewModel.setBeneficiaryFormForEditing(item)
                                    isFormMode = true
                                },
                                onDelete = { beneficiaryToDelete = item },
                                onTransfer = {
                                    viewModel.clearTransferForm()
                                    viewModel.transferSelectedBeneficiary = item
                                    viewModel.navigateTo(Screen.FundTransfer(item))
                                }
                            )
                        }
                    }
                }
            }
        }

        // Deletion confirmation modal
        if (beneficiaryToDelete != null) {
            val toDelete = beneficiaryToDelete!!
            AlertDialog(
                onDismissRequest = { beneficiaryToDelete = null },
                title = { Text("Confirm Deletion") },
                text = {
                    Text("Are you sure you want to delete ${toDelete.name} (A/c: ${toDelete.accountNumber}) from your beneficiary register?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteBeneficiaryItem(toDelete)
                            beneficiaryToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("REMOVE", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { beneficiaryToDelete = null }) {
                        Text("CANCEL", color = SbiTextSecondary)
                    }
                }
            )
        }
    }
}

// Beneficiary list item card
@Composable
fun BeneficiaryRow(
    beneficiary: Beneficiary,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTransfer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, SbiBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large initial circular avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SbiPrimary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = beneficiary.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SbiPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = beneficiary.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiTextDark
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(SbiSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = beneficiary.nickName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SbiPrimaryDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "A/c: ${beneficiary.accountNumber} | IFSC: ${beneficiary.ifsc}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SbiTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "Daily Trf Limit: ₹${String.format("%,.2f", beneficiary.limitAmount)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SbiAccentOrange
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SbiBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action row buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit details",
                            tint = SbiPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete beneficiary",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // PAY / TRANSFER Button
                Button(
                    onClick = onTransfer,
                    colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRANSFER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
