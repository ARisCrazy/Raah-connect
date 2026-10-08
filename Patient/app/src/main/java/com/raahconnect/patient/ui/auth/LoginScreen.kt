package com.raahconnect.patient.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.raahconnect.patient.ui.theme.EmergencyRed
import com.raahconnect.patient.ui.theme.NeutralBackground
import com.raahconnect.patient.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeutralBackground)
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // RAAH CONNECT Brand Header
            AuthHeader(
                title = "Welcome Back",
                subtitle = "Sign in to access emergency medical services & patient care"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Banner
            AuthErrorBanner(errorMessage = state.generalError)

            Spacer(modifier = Modifier.height(8.dp))

            // Email Field
            AuthTextField(
                value = state.email,
                onValueChange = viewModel::onLoginEmailChange,
                label = "Email Address",
                placeholder = "patient@example.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field with Visibility Toggle
            AuthPasswordField(
                value = state.password,
                onValueChange = viewModel::onLoginPasswordChange,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibility = viewModel::toggleLoginPasswordVisibility,
                errorMessage = state.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login(onSuccess = onLoginSuccess)
                    }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            AuthPrimaryButton(
                text = "Login",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(onSuccess = onLoginSuccess)
                },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Create Account Secondary Button
            OutlinedButton(
                onClick = onNavigateToRegister,
                enabled = !state.isLoading,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EmergencyRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = EmergencyRed
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New to RAAH Connect? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Register here",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = EmergencyRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
