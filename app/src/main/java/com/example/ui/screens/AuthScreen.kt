package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ReceiptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: ReceiptViewModel,
    onNavigateToMain: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Pulse animation for NexReceipt logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Deep modern charcoal/blue aesthetic
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF13151A),
            Color(0xFF1E2640),
            Color(0xFF0F1115)
        )
    )

    val neonBlue = Color(0xFF4A90E2)
    val neonTeal = Color(0xFF50E3C2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Header Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(neonBlue, neonTeal))
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ReceiptLong,
                    contentDescription = "Logo Icon",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Title & Tagline
            Text(
                text = "NexReceipt Studio X",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Next-Gen Invoice & Receipt Designer",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Input Fields
            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = neonBlue) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = neonBlue,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("auth_name_field"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = neonBlue) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = neonBlue,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("auth_email_field"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = neonBlue) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = neonBlue,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .testTag("auth_password_field"),
                shape = RoundedCornerShape(12.dp)
            )

            // Primary Action Button (Sign In / Sign Up)
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        errorMessage = "Please enter your email."
                        return@Button
                    }
                    if (password.length < 4) {
                        errorMessage = "Password must be at least 4 characters."
                        return@Button
                    }
                    val finalName = if (isSignUp) name.ifEmpty { "New Member" } else email.substringBefore("@")
                    
                    // Trigger login using ViewModel
                    viewModel.login(
                        email = email,
                        name = finalName,
                        companyName = "NexStudio INC",
                        isAdmin = email.contains("admin") || email == "princearabdurrahman57@gmail.com"
                    )
                    onNavigateToMain()
                },
                colors = ButtonDefaults.buttonColors(containerColor = neonBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("auth_action_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isSignUp) "Create Business ID" else "Secure Login",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Login Options Divider
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.12f))
                Text(
                    text = "OR",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.12f))
            }

            // Google & Guest Sign In mock options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.login(
                            email = "princearabdurrahman57@gmail.com",
                            name = "Prince AR Abdur Rahman",
                            companyName = "NexVora Lab's Ofc",
                            isAdmin = true
                        )
                        onNavigateToMain()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin login",
                        tint = neonTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Admin Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.loginAsGuest()
                        onNavigateToMain()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Guest login",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guest Mode", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Switch Form Toggle
            Text(
                text = if (isSignUp) "Already a studio member? Log In" else "Create a Studio Account",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = neonTeal
                ),
                modifier = Modifier
                    .clickable { isSignUp = !isSignUp }
                    .testTag("auth_mode_toggle")
            )
        }
    }
}
