package com.raahconnect.patient.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.raahconnect.patient.data.model.EmergencyRequest

class EmergencyRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Creates a new emergency request document in Firestore at emergencyRequests/{id}.
     */
    suspend fun createEmergencyRequest(
        forWhom: String,
        emergencyType: String,
        latitude: Double,
        longitude: Double
    ): Result<EmergencyRequest> {
        val currentUser = auth.currentUser
            ?: return Result.failure(Exception("Patient session expired. Please log in again."))

        val patientId = currentUser.uid

        return try {
            val docRef = firestore.collection("emergencyRequests").document()
            val emergencyId = docRef.id

            val emergencyData = hashMapOf(
                "patientId" to patientId,
                "forWhom" to forWhom,
                "emergencyType" to emergencyType,
                "latitude" to latitude,
                "longitude" to longitude,
                "status" to "REQUESTED",
                "createdAt" to FieldValue.serverTimestamp(),
                "ambulanceId" to null,
                "hospitalId" to null,
                "assignmentId" to null
            )

            docRef.set(emergencyData).awaitResult()

            val createdRequest = EmergencyRequest(
                id = emergencyId,
                patientId = patientId,
                forWhom = forWhom,
                emergencyType = emergencyType,
                latitude = latitude,
                longitude = longitude,
                status = "REQUESTED"
            )

            Result.success(createdRequest)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: "Failed to create emergency request. Please try again."
            Result.failure(Exception(message, e))
        }
    }
}
