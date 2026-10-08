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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.registerState.collectAsState()
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
            Spacer(modifier = Modifier.height(12.dp))

            // RAAH CONNECT Brand Header
            AuthHeader(
                title = "Create Patient Account",
                subtitle = "Register to connect with rapid emergency medical assistance"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner
            AuthErrorBanner(errorMessage = state.generalError)

            Spacer(modifier = Modifier.height(4.dp))

            // Full Name Field
            AuthTextField(
                value = state.fullName,
                onValueChange = viewModel::onRegisterNameChange,
                label = "Full Name",
                placeholder = "e.g. Rahul Sharma",
                leadingIcon = Icons.Default.Person,
                errorMessage = state.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mobile Number Field
            AuthTextField(
                value = state.phone,
                onValueChange = viewModel::onRegisterPhoneChange,
                label = "Mobile Number (10 digits)",
                placeholder = "9876543210",
                leadingIcon = Icons.Default.Phone,
                errorMessage = state.phoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email Address Field
            AuthTextField(
                value = state.email,
                onValueChange = viewModel::onRegisterEmailChange,
                label = "Email Address",
                placeholder = "name@example.com",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Password Field
            AuthPasswordField(
                value = state.password,
                onValueChange = viewModel::onRegisterPasswordChange,
                label = "Password (min. 6 characters)",
                leadingIcon = Icons.Default.Lock,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibility = viewModel::toggleRegisterPasswordVisibility,
                errorMessage = state.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirm Password Field
            AuthPasswordField(
                value = state.confirmPassword,
                onValueChange = viewModel::onRegisterConfirmPasswordChange,
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                isPasswordVisible = state.isConfirmPasswordVisible,
                onToggleVisibility = viewModel::toggleRegisterConfirmPasswordVisibility,
                errorMessage = state.confirmPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.register(onSuccess = onRegisterSuccess)
                    }
                ),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Account Button
            AuthPrimaryButton(
                text = "Create Account",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.register(onSuccess = onRegisterSuccess)
                },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Already have an account? Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Login",
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
