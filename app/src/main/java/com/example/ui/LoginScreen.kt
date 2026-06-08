package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SbiAccentOrange
import com.example.ui.theme.SbiPrimary
import com.example.ui.theme.SbiSurfaceLight
import com.example.ui.theme.SbiPrimaryDark
import com.example.ui.theme.SbiTextSecondary
import com.example.ui.theme.SbiBorder
import com.example.ui.theme.SbiTextDark
import com.example.viewmodel.BankViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: BankViewModel, modifier: Modifier = Modifier) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Main Logo Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Draw SBI Logo
                SbiLogoCanvas(modifier = Modifier.size(54.dp))
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "State Bank of India",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SbiPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "retail.onlinesbi.sbi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SbiAccentOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Welcome Text Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SbiPrimary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SbiPrimary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Personal Banking Login",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SbiPrimary
                        )
                    )
                    Text(
                        text = "Welcome to the secure retail banking portal. Please provide your credential parameters below to establish a session.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Credential Inputs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "SECURE SIGN IN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SbiPrimary,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Username Input
                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = { viewModel.username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        label = { Text("Username") },
                        placeholder = { Text("Enter your netbanking username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SbiPrimary,
                            focusedLabelColor = SbiPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        label = { Text("Login Password") },
                        placeholder = { Text("Enter account password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                        trailingIcon = {
                            Text(
                                text = if (passwordVisible) "HIDE" else "SHOW",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SbiPrimary
                                ),
                                modifier = Modifier
                                    .clickable { passwordVisible = !passwordVisible }
                                    .padding(8.dp)
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SbiPrimary,
                            focusedLabelColor = SbiPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Captcha Label and Code
                    Text(
                        text = "Security Verification Captcha",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SbiPrimary
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom Drawn Captcha Box
                        CaptchaCanvasView(
                            captchaValue = viewModel.realCaptcha,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, SbiBorder, RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Refresh Button
                        IconButton(
                            onClick = { viewModel.regenerateCaptcha() },
                            modifier = Modifier.background(SbiPrimary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh Captcha",
                                tint = SbiPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Captcha Text Field
                    OutlinedTextField(
                        value = viewModel.captchaInput,
                        onValueChange = { viewModel.captchaInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captcha_input"),
                        label = { Text("Enter Captcha Code") },
                        placeholder = { Text("Enter exact code shown above") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SbiPrimary,
                            focusedLabelColor = SbiPrimary
                        )
                    )

                    // Error presentation
                    if (viewModel.loginError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert icon",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = viewModel.loginError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Submit Button
                    Button(
                        onClick = { viewModel.submitLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LOGIN SECURELY",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security Directives Panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SbiSurfaceLight),
                border = BorderStroke(1.dp, SbiBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Shield Guard",
                            tint = SbiAccentOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MANDATORY SECURITY DIRECTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SbiPrimaryDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val safetyBulletins = listOf(
                        "SBI customer support NEVER requests your OTP PIN, Login Passphrase, CVV, or Debit Card grid coordinates.",
                        "Verify that your browser bar displays https://retail.onlinesbi.sbi with a closed padlock icon.",
                        "Report suspicious URLs or phishing attempts straight to report.phishing@sbi.co.in immediately."
                    )

                    safetyBulletins.forEach { bulletin ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SbiAccentOrange
                                )
                            )
                            Text(
                                text = bulletin,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    color = SbiTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // OTP Simulated dialog modal overlay
        if (viewModel.simulatedOtpSent) {
            Dialog(
                onDismissRequest = { viewModel.simulatedOtpSent = false },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SbiPrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing miniature secure key logo
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(SbiPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Shield key icon",
                                tint = SbiPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "High Security OTP Verification",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SbiPrimary,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "An SMS One-Time Password (OTP) was dispatched to your pre-verified mobile coordinate ********12. Enter the PIN below.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SbiTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // OTP Field input
                        OutlinedTextField(
                            value = viewModel.otpInput,
                            onValueChange = { viewModel.otpInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_otp_input"),
                            label = { Text("Enter 6-Digit OTP") },
                            placeholder = { Text("Demo code is anything (e.g., 123456)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SbiPrimary,
                                focusedLabelColor = SbiPrimary
                            )
                        )

                        if (viewModel.otpError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.otpError,
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
                                    viewModel.simulatedOtpSent = false
                                    viewModel.otpInput = ""
                                }
                            ) {
                                Text("CANCEL", color = SbiTextSecondary)
                            }

                            Button(
                                onClick = { viewModel.verifyLoginOtp() },
                                colors = ButtonDefaults.buttonColors(containerColor = SbiPrimary)
                            ) {
                                Text("SUBMIT LOGIN", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom Draw SbiLogo for spectacular vector branding
@Composable
fun SbiLogoCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.width / 2
        val strokeWidth = outerRadius * 0.45f
        val innerCircleRadius = outerRadius - (strokeWidth / 2)

        // Draw primary cyan circular arc
        drawCircle(
            color = Color(0xFF00B2EC),
            radius = innerCircleRadius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth
            )
        )

        // Draw vertical gap at vertical base
        val gapWidth = outerRadius * 0.28f
        drawLine(
            color = Color.White,
            start = Offset(center.x, center.y + innerCircleRadius - strokeWidth),
            end = Offset(center.x, center.y + outerRadius),
            strokeWidth = gapWidth,
            cap = StrokeCap.Butt
        )
    }
}

// Captcha Canvas: Generates a noisy canvas with lines and circles, overlaying letters with high-security distortion
@Composable
fun CaptchaCanvasView(captchaValue: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFE8EEF1)),
        contentAlignment = Alignment.Center
    ) {
        // Draw backgrounds noises on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Seed a deterministic random to get consistent line styling during recomposition of same captcha string
            val seed = captchaValue.hashCode().toLong()
            val random = Random(seed)

            // Draw noise lines
            for (i in 1..8) {
                drawLine(
                    color = Color.LightGray.copy(alpha = random.nextFloat() * 0.6f + 0.3f),
                    start = Offset(random.nextFloat() * width, random.nextFloat() * height),
                    end = Offset(random.nextFloat() * width, random.nextFloat() * height),
                    strokeWidth = random.nextFloat() * 4f + 1f
                )
            }

            // Draw noise circles
            for (i in 1..6) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = random.nextFloat() * 0.4f + 0.2f),
                    radius = random.nextFloat() * 20.dp.toPx() + 5.dp.toPx(),
                    center = Offset(random.nextFloat() * width, random.nextFloat() * height)
                )
            }
        }

        // Draw the actual Captcha letters
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val seed = captchaValue.hashCode().toLong()
            val letterRandom = Random(seed)

            captchaValue.forEach { char ->
                val rotation = (letterRandom.nextFloat() * 30f) - 15f
                val scale = (letterRandom.nextFloat() * 0.4f) + 0.9f
                val color = when (letterRandom.nextInt(3)) {
                    0 -> Color(0xFF0F3A50)
                    1 -> Color(0xFF008D6B)
                    else -> Color(0xFFAC5D00)
                }

                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = if (letterRandom.nextBoolean()) FontStyle.Italic else FontStyle.Normal,
                        fontSize = (26 * scale).sp,
                        letterSpacing = 2.sp,
                        color = color
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}
