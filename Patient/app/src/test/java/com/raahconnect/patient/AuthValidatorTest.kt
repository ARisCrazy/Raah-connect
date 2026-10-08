package com.raahconnect.patient

import com.raahconnect.patient.data.validation.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `validateName succeeds for valid names`() {
        assertNull(AuthValidator.validateName("Rahul Sharma"))
        assertNull(AuthValidator.validateName("A B"))
    }

    @Test
    fun `validateName fails for empty or single char name`() {
        assertNotNull(AuthValidator.validateName(""))
        assertNotNull(AuthValidator.validateName("   "))
        assertNotNull(AuthValidator.validateName("A"))
    }

    @Test
    fun `validatePhone succeeds for valid 10-digit Indian numbers`() {
        assertNull(AuthValidator.validatePhone("9876543210"))
        assertNull(AuthValidator.validatePhone("+91 9876543210"))
        assertNull(AuthValidator.validatePhone("09876543210"))
        assertNull(AuthValidator.validatePhone("918876543210"))
        assertNull(AuthValidator.validatePhone("7890123456"))
        assertNull(AuthValidator.validatePhone("6123456789"))
    }

    @Test
    fun `validatePhone fails for invalid Indian numbers`() {
        assertNotNull(AuthValidator.validatePhone(""))
        assertNotNull(AuthValidator.validatePhone("12345"))
        assertNotNull(AuthValidator.validatePhone("5555555555")) // Indian mobile numbers start with 6-9
        assertNotNull(AuthValidator.validatePhone("987654321")) // 9 digits
        assertNotNull(AuthValidator.validatePhone("987654321000")) // Too long
    }

    @Test
    fun `formatIndianPhone formats numbers correctly`() {
        assertEquals("+919876543210", AuthValidator.formatIndianPhone("9876543210"))
        assertEquals("+919876543210", AuthValidator.formatIndianPhone("+91 9876543210"))
        assertEquals("+919876543210", AuthValidator.formatIndianPhone("09876543210"))
    }

    @Test
    fun `validateEmail succeeds for valid email`() {
        assertNull(AuthValidator.validateEmail("patient@example.com"))
        assertNull(AuthValidator.validateEmail("user.name+tag@domain.co.in"))
    }

    @Test
    fun `validateEmail fails for invalid email`() {
        assertNotNull(AuthValidator.validateEmail(""))
        assertNotNull(AuthValidator.validateEmail("notanemail"))
        assertNotNull(AuthValidator.validateEmail("missing@domain"))
    }

    @Test
    fun `validatePassword succeeds for 6 or more characters`() {
        assertNull(AuthValidator.validatePassword("123456"))
        assertNull(AuthValidator.validatePassword("SecurePass!123"))
    }

    @Test
    fun `validatePassword fails for under 6 characters`() {
        assertNotNull(AuthValidator.validatePassword(""))
        assertNotNull(AuthValidator.validatePassword("12345"))
    }

    @Test
    fun `validateConfirmPassword succeeds when matching`() {
        assertNull(AuthValidator.validateConfirmPassword("password123", "password123"))
    }

    @Test
    fun `validateConfirmPassword fails when mismatching`() {
        assertNotNull(AuthValidator.validateConfirmPassword("password123", "password321"))
        assertNotNull(AuthValidator.validateConfirmPassword("password123", ""))
    }
}
