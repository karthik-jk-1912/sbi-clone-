package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.BankViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: BankViewModel, modifier: Modifier = Modifier) {
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    
    var isBalanceVisible by remember { mutableStateOf(true) }
    var showDemoDepositDialog by remember { mutableStateOf(false) }
    var demoAmtStr by remember { mutableStateOf("10000") }
    var demoDescStr by remember { mutableStateOf("Salary Bonus Credited") }
    var demoCatStr by remember { mutableStateOf("Salary") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SbiLogoCanvas(modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "State Bank of India",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.performLogout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Secure Logout",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // 1. Account welcome details card & Real-time Balance
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = SbiPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "OnlineSBI Retail Profile",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Hi, ${viewModel.username}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SbiAccentOrange
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "PRIMARY ACCOUNT NUMBER",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f))
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "3009 1873 612",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SBI SAVINGS",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SbiSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Privacy protected balance visualization
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "AVAILABLE REF BALANCE",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f))
                                    )
                                    Text(
                                        text = if (isBalanceVisible) {
                                            "₹" + String.format("%,.2f", currentBalance)
                                        } else {
                                            "₹ ••••••••"
                                        },
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontSize = 24.sp
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = { isBalanceVisible = !isBalanceVisible },
                                    modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Toggle Balance Privacy State",
                                        tint = if (isBalanceVisible) Color.White else Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Interactive Navigation Actions Grid
                item {
                    Text(
                        text = "QUICK BANKING ACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SbiTextSecondary,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Transfer Funds",
                            desc = "IMPS / NEFT / RTGS transfer",
                            icon = Icons.Default.Send,
                            color = SbiPrimary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_transfer_funds")
                        ) {
                            viewModel.clearTransferForm()
                            viewModel.navigateTo(Screen.FundTransfer())
                        }

                        ActionCard(
                            title = "Beneficiaries",
                            desc = "Add, edit or delete",
                            icon = Icons.Default.Person,
                            color = SbiSecondary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_manage_benef")
                        ) {
                            viewModel.clearBeneficiaryForm()
                            viewModel.navigateTo(Screen.BeneficiariesList)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Detailed Statement",
                            desc = "Search and filter statement logs",
                            icon = Icons.Default.List,
                            color = SbiAccentOrange,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_detailed_statement")
                        ) {
                            viewModel.navigateTo(Screen.TransactionHistory)
                        }

                        ActionCard(
                            title = "Quick Credit (Demo)",
                            desc = "Add credit deposit sum",
                            icon = Icons.Default.Add,
                            color = Color(0xFF008D6B),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_demo_deposit")
                        ) {
                            showDemoDepositDialog = true
                        }
                    }
                }

                // 3. Mini-Statement (last 5-10 transactions)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MINI-STATEMENT (LAST RECENT)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SbiTextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "VIEW DETAILED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiPrimary,
                            ),
                            modifier = Modifier
                                .clickable {
                                    viewModel.navigateTo(Screen.TransactionHistory)
                                }
                                .padding(4.dp)
                        )
                    }
                }

                // Slice the last 8 items to fulfill "view their last 5-10 transactions" mandate
                val miniStatementList = transactions.take(8)

                if (miniStatementList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SbiBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Info icon",
                                    tint = SbiTextSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No recent transactions found.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SbiTextSecondary)
                                )
                            }
                        }
                    }
                } else {
                    items(miniStatementList) { item ->
                        MiniStatementRow(transaction = item)
                    }
                }
            }
        }

        // Demo deposit dialog
        if (showDemoDepositDialog) {
            Dialog(onDismissRequest = { showDemoDepositDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SbiPrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Simulate Inward Credit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = demoAmtStr,
                            onValueChange = { demoAmtStr = it },
                            label = { Text("Deposit Amount (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = demoDescStr,
                            onValueChange = { demoDescStr = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Salary", "Deposit", "Refund").forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (demoCatStr == cat) SbiPrimary else SbiPrimary.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { demoCatStr = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (demoCatStr == cat) Color.White else SbiPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { showDemoDepositDialog = false }) {
                                Text("CANCEL", color = SbiTextSecondary)
                            }
                            Button(
                                onClick = {
                                    val amt = demoAmtStr.toDoubleOrNull() ?: 1000.0
                                    viewModel.simulateCredit(amt, demoDescStr, demoCatStr)
                                    showDemoDepositDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary)
                            ) {
                                Text("CREDIT NOW")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Reusable action card with standard shapes, ripples and iconography
@Composable
fun ActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SbiBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SbiTextDark
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = SbiTextSecondary,
                        lineHeight = 13.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Mini Statement list row
@Composable
fun MiniStatementRow(transaction: com.example.data.Transaction) {
    val dateStr = remember(transaction.timestamp) {
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        formatter.format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SbiBorder.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category circular bullet indicator
                val catColor = when (transaction.category) {
                    "Salary" -> Color(0xFF008D6B)
                    "Bills" -> Color(0xFFFF9900)
                    "Transfer" -> SbiPrimary
                    "Cash" -> Color(0xFF673AB7)
                    "Shopping" -> Color(0xFFE91E63)
                    else -> SbiTextSecondary
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(catColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (transaction.category) {
                        "Salary" -> Icons.Default.Star
                        "Bills" -> Icons.Default.Info
                        "Transfer" -> Icons.Default.Send
                        "Cash" -> Icons.Default.Home
                        "Shopping" -> Icons.Default.Star
                        else -> Icons.Default.Info
                    }
                    Icon(
                        icon,
                        contentDescription = "Tx Icon",
                        tint = catColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SbiTextDark
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$dateStr • Ref: ${transaction.refNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SbiTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount with relative sign and color shading
            val prefix = if (transaction.type == "CREDIT") "+" else "-"
            val amtColor = if (transaction.type == "CREDIT") Color(0xFF008D6B) else SbiTextDark

            Text(
                text = "$prefix₹${String.format("%,.2f", transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = amtColor
                )
            )
        }
    }
}
