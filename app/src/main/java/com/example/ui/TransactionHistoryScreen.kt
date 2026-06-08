package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.ui.theme.*
import com.example.viewmodel.BankViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(viewModel: BankViewModel, modifier: Modifier = Modifier) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    var showSortMenu by remember { mutableStateOf(false) }

    // local filters matching UI state variables in viewModel or locally derived
    val filteredAndSortedTxList = remember(transactions, viewModel.txSearchQuery, viewModel.txSortBy, viewModel.txFilterByCategory) {
        var list = transactions.filter { tx ->
            val matchQuery = tx.description.contains(viewModel.txSearchQuery, ignoreCase = true) ||
                    tx.refNumber.contains(viewModel.txSearchQuery, ignoreCase = true)
            val matchCategory = viewModel.txFilterByCategory == "ALL" || tx.category.equals(viewModel.txFilterByCategory, ignoreCase = true)
            matchQuery && matchCategory
        }

        // Apply selected sort type
        list = when (viewModel.txSortBy) {
            "DATE_ASC" -> list.sortedBy { it.timestamp }
            "AMOUNT_DESC" -> list.sortedByDescending { it.amount }
            "AMOUNT_ASC" -> list.sortedBy { it.amount }
            else -> list.sortedByDescending { it.timestamp } // DATE_DESC
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Statement",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("tx_history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Quick clear or summary helper
                    IconButton(onClick = { viewModel.txSearchQuery = "" }) {
                        Icon(Icons.Default.Menu, contentDescription = "Active filters", tint = Color.White)
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
            // Search Input box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SbiPrimary)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.txSearchQuery,
                    onValueChange = { viewModel.txSearchQuery = it },
                    placeholder = { Text("Search description or SBI Ref No...", color = Color.White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color.White) },
                    trailingIcon = {
                        if (viewModel.txSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.txSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("statement_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            // Quick Sort or Category Selector bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip selector
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FILTERS: Category = ${viewModel.txFilterByCategory}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SbiTextSecondary
                        )
                    )
                }

                // Sort anchor button
                Box {
                    Button(
                        onClick = { showSortMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary.copy(alpha = 0.08f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val sortLabel = when (viewModel.txSortBy) {
                                "DATE_ASC" -> "Oldest first"
                                "AMOUNT_DESC" -> "Highest Sum"
                                "AMOUNT_ASC" -> "Lowest Sum"
                                else -> "Newest first"
                            }
                            Text(
                                text = "Sort: $sortLabel",
                                style = MaterialTheme.typography.labelMedium.copy(color = SbiPrimary, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort selection indicator", tint = SbiPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest Transactions First", color = SbiTextDark) },
                            onClick = {
                                viewModel.txSortBy = "DATE_DESC"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Oldest Transactions First", color = SbiTextDark) },
                            onClick = {
                                viewModel.txSortBy = "DATE_ASC"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount: Highest to Lowest", color = SbiTextDark) },
                            onClick = {
                                viewModel.txSortBy = "AMOUNT_DESC"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Amount: Lowest to Highest", color = SbiTextDark) },
                            onClick = {
                                viewModel.txSortBy = "AMOUNT_ASC"
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            // Quick Category selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categoryFilters = listOf("ALL", "Transfer", "Salary", "Bills", "Shopping", "Cash")
                categoryFilters.forEach { cat ->
                    val isSelected = viewModel.txFilterByCategory == cat
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) SbiPrimary else SbiPrimary.copy(alpha = 0.05f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.txFilterByCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else SbiPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transaction results list
            if (filteredAndSortedTxList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No matching search transactions",
                            tint = SbiTextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching transactions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiTextDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try modifying search queries or clearing active filtering tabs.",
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
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(filteredAndSortedTxList) { tx ->
                        DetailedTransactionRow(transaction = tx)
                    }
                }
            }
        }
    }
}

// Full size beautiful transaction record row
@Composable
fun DetailedTransactionRow(transaction: Transaction) {
    var isExpanded by remember { mutableStateOf(false) }

    val dateStr = remember(transaction.timestamp) {
        val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
        formatter.format(java.util.Date(transaction.timestamp))
    }
    val timeStr = remember(transaction.timestamp) {
        val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        formatter.format(java.util.Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SbiBorder.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            .size(36.dp)
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
                        Icon(icon, contentDescription = "Tx Icon", tint = catColor, modifier = Modifier.size(18.dp))
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
                            text = "$dateStr at $timeStr",
                            style = MaterialTheme.typography.labelSmall.copy(color = SbiTextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Value showing
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

            // Expose detailed collapsible metadata details! Gives professional desktop status look on click!
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = SbiBorder.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SBI UTR Reference No", style = MaterialTheme.typography.labelSmall.copy(color = SbiTextSecondary))
                        Text(transaction.refNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Category / Flow Type", style = MaterialTheme.typography.labelSmall.copy(color = SbiTextSecondary))
                        Box(
                            modifier = Modifier
                                .background(SbiPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                transaction.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SbiPrimary, fontSize = 9.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
