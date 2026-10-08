package com.raahconnect.patient.data.model

import com.google.firebase.Timestamp

data class PatientUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "PATIENT",
    val createdAt: Timestamp? = null
)
