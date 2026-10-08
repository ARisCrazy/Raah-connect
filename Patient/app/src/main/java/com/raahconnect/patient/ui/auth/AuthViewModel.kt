package com.raahconnect.patient.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raahconnect.patient.data.repository.AuthRepository
import com.raahconnect.patient.data.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

data class RegisterUiState(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class AuthViewModel @JvmOverloads constructor(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = repository.isUserLoggedIn

    // ----------------------------------------------------
    // Login Handlers
    // ----------------------------------------------------
    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun toggleLoginPasswordVisibility() {
        _loginState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login(onSuccess: () -> Unit) {
        val email = _loginState.value.email.trim()
        val password = _loginState.value.password

        val emailError = AuthValidator.validateEmail(email)
        val passwordError = if (password.isBlank()) "Please enter your password." else null

        if (emailError != null || passwordError != null) {
            _loginState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _loginState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val result = repository.login(email, password)
            result.fold(
                onSuccess = {
                    _loginState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            generalError = error.message ?: "Login failed. Please try again."
                        )
                    }
                }
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginUiState()
    }

    // ----------------------------------------------------
    // Register Handlers
    // ----------------------------------------------------
    fun onRegisterNameChange(name: String) {
        _registerState.update { it.copy(fullName = name, nameError = null, generalError = null) }
    }

    fun onRegisterPhoneChange(phone: String) {
        _registerState.update { it.copy(phone = phone, phoneError = null, generalError = null) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onRegisterConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null) }
    }

    fun toggleRegisterPasswordVisibility() {
        _registerState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        _registerState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun register(onSuccess: () -> Unit) {
        val name = _registerState.value.fullName
        val phone = _registerState.value.phone
        val email = _registerState.value.email
        val password = _registerState.value.password
        val confirmPassword = _registerState.value.confirmPassword

        val nameErr = AuthValidator.validateName(name)
        val phoneErr = AuthValidator.validatePhone(phone)
        val emailErr = AuthValidator.validateEmail(email)
        val passwordErr = AuthValidator.validatePassword(password)
        val confirmPasswordErr = AuthValidator.validateConfirmPassword(password, confirmPassword)

        if (nameErr != null || phoneErr != null || emailErr != null || passwordErr != null || confirmPasswordErr != null) {
            _registerState.update {
                it.copy(
                    nameError = nameErr,
                    phoneError = phoneErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr
                )
            }
            return
        }

        val formattedPhone = AuthValidator.formatIndianPhone(phone)
        _registerState.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val result = repository.register(name, formattedPhone, email, password)
            result.fold(
                onSuccess = {
                    _registerState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _registerState.update {
                        it.copy(
                            isLoading = false,
                            generalError = error.message ?: "Registration failed. Please try again."
                        )
                    }
                }
            )
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterUiState()
    }
}
