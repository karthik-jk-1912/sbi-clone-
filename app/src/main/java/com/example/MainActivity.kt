package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BeneficiaryScreen
import com.example.ui.HomeScreen
import com.example.ui.LoginScreen
import com.example.ui.TransactionHistoryScreen
import com.example.ui.TransferScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BankViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: BankViewModel = viewModel()
                val currentScreen = viewModel.currentScreen

                // Global system back interceptor
                BackHandler(enabled = currentScreen != Screen.Login) {
                    if (currentScreen == Screen.MainDashboard) {
                        viewModel.performLogout()
                    } else {
                        viewModel.navigateBack()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.MainDashboard -> {
                            HomeScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.BeneficiariesList -> {
                            BeneficiaryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.AddEditBeneficiary -> {
                            BeneficiaryScreen(
                                viewModel = viewModel,
                                editBeneficiary = screen.beneficiary,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.FundTransfer -> {
                            TransferScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is Screen.TransactionHistory -> {
                            TransactionHistoryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
