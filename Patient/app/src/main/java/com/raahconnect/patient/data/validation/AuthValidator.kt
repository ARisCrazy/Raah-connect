package com.raahconnect.patient.data.validation

object AuthValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> "Full name is required."
            trimmed.length < 2 -> "Please enter a valid full name."
            else -> null
        }
    }

    fun validatePhone(phone: String): String? {
        val trimmed = phone.trim()
        if (trimmed.isBlank()) {
            return "Mobile number is required."
        }

        val digits = trimmed.replace(Regex("[^0-9]"), "")
        val tenDigits = extractTenDigitIndianPhone(digits)

        return when {
            tenDigits == null -> "Please enter a valid 10-digit Indian mobile number."
            !tenDigits.first().let { it in '6'..'9' } -> "Indian mobile number must start with 6, 7, 8, or 9."
            else -> null
        }
    }

    fun extractTenDigitIndianPhone(digits: String): String? {
        return when {
            digits.length == 10 -> digits
            digits.length == 12 && digits.startsWith("91") -> digits.substring(2)
            digits.length == 11 && digits.startsWith("0") -> digits.substring(1)
            else -> null
        }
    }

    fun formatIndianPhone(phone: String): String {
        val digits = phone.trim().replace(Regex("[^0-9]"), "")
        val tenDigits = extractTenDigitIndianPhone(digits) ?: digits
        return "+91$tenDigits"
    }

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isBlank() -> "Email address is required."
            !EMAIL_REGEX.matches(trimmed) -> "Please enter a valid email address."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required."
            password.length < 6 -> "Password must be at least 6 characters long."
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password."
            confirmPassword != password -> "Passwords do not match."
            else -> null
        }
    }
}
