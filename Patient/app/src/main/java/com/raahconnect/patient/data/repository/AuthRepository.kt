package com.raahconnect.patient.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.raahconnect.patient.data.model.PatientUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUid: String?
        get() = auth.currentUser?.uid

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password)
                .awaitResult()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed. Please try again."))
            }
        } catch (e: Exception) {
            Result.failure(Exception(getFriendlyErrorMessage(e), e))
        }
    }

    /**
     * Registers a new patient with Firebase Authentication and saves
     * their profile in Firestore at users/{uid}.
     */
    suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Result<PatientUser> {
        return try {
            // 1. Create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password)
                .awaitResult()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("User creation failed. Please try again."))

            val uid = firebaseUser.uid

            // 2. Prepare patient profile in users/{uid}
            val userDocument = hashMapOf<String, Any>(
                "name" to name.trim(),
                "email" to email.trim(),
                "phone" to phone.trim(),
                "role" to "PATIENT",
                "createdAt" to FieldValue.serverTimestamp()
            )

            // 3. Save to Firestore users collection
            firestore.collection("users")
                .document(uid)
                .set(userDocument)
                .awaitResult()

            val patientUser = PatientUser(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                role = "PATIENT"
            )

            Result.success(patientUser)
        } catch (e: Exception) {
            Result.failure(Exception(getFriendlyErrorMessage(e), e))
        }
    }

    /**
     * Fetches the patient profile from Firestore: users/{uid}
     */
    suspend fun getPatientProfile(uid: String): Result<PatientUser> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .awaitResult()

            if (snapshot.exists()) {
                val patientUser = PatientUser(
                    uid = uid,
                    name = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: (auth.currentUser?.email ?: ""),
                    phone = snapshot.getString("phone") ?: "",
                    role = snapshot.getString("role") ?: "PATIENT",
                    createdAt = snapshot.getTimestamp("createdAt")
                )
                Result.success(patientUser)
            } else {
                // Fallback using auth user info if doc not yet propagated
                val fallbackUser = PatientUser(
                    uid = uid,
                    name = auth.currentUser?.displayName ?: "Patient",
                    email = auth.currentUser?.email ?: "",
                    phone = "",
                    role = "PATIENT"
                )
                Result.success(fallbackUser)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not load patient profile: ${e.localizedMessage ?: "Unknown error"}", e))
        }
    }

    /**
     * Signs out the current user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Translates Firebase exceptions into clear, friendly messages.
     */
    private fun getFriendlyErrorMessage(e: Throwable): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address is improperly formatted."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                    else -> "Invalid email or password. Please verify your credentials."
                }
            }
            is FirebaseAuthInvalidUserException -> {
                when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please create an account."
                    "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support."
                    else -> "No account found with this email address."
                }
            }
            is FirebaseAuthUserCollisionException -> {
                "An account with this email already exists. Please log in instead."
            }
            is FirebaseAuthWeakPasswordException -> {
                "Password is too weak. Please use at least 6 characters."
            }
            is FirebaseNetworkException -> {
                "Network connection error. Please check your internet connectivity."
            }
            is FirebaseAuthException -> {
                val code = e.errorCode
                when {
                    code.contains("TOO_MANY_REQUESTS", ignoreCase = true) ->
                        "Too many attempts. Please wait a few moments and try again."
                    code.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                        "Invalid email or password. Please check your credentials."
                    code.contains("USER_NOT_FOUND", ignoreCase = true) ->
                        "No account found with this email. Please register first."
                    code.contains("WRONG_PASSWORD", ignoreCase = true) ->
                        "Incorrect password. Please try again."
                    code.contains("EMAIL_ALREADY_IN_USE", ignoreCase = true) ->
                        "This email is already registered. Please login."
                    else -> e.localizedMessage ?: "Authentication failed. Please try again."
                }
            }
            else -> {
                val message = e.localizedMessage ?: e.message ?: ""
                when {
                    message.contains("network", ignoreCase = true) ->
                        "Network connection error. Please check your internet."
                    message.contains("password", ignoreCase = true) && message.contains("invalid", ignoreCase = true) ->
                        "Invalid email or password. Please check your credentials."
                    message.isNotBlank() -> message
                    else -> "An unexpected error occurred. Please try again."
                }
            }
        }
    }
}

/**
 * Extension function to await a Firebase Task with coroutine cancellation support.
 */
suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
