package com.raahconnect.patient.data.model

import com.google.firebase.Timestamp

data class EmergencyRequest(
    val id: String = "",
    val patientId: String = "",
    val forWhom: String = "", // "SELF" or "OTHER"
    val emergencyType: String = "", // "CARDIAC" or "ACCIDENT"
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "REQUESTED",
    val createdAt: Timestamp? = null,
    val ambulanceId: String? = null,
    val hospitalId: String? = null,
    val assignmentId: String? = null
)
