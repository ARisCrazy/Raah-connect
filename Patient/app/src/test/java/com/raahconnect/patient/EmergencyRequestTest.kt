package com.raahconnect.patient

import com.raahconnect.patient.data.model.EmergencyRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmergencyRequestTest {

    @Test
    fun `default emergency request has expected values`() {
        val request = EmergencyRequest()
        assertEquals("", request.id)
        assertEquals("", request.patientId)
        assertEquals("", request.forWhom)
        assertEquals("", request.emergencyType)
        assertEquals(0.0, request.latitude, 0.0001)
        assertEquals(0.0, request.longitude, 0.0001)
        assertEquals("REQUESTED", request.status)
        assertNull(request.createdAt)
        assertNull(request.ambulanceId)
        assertNull(request.hospitalId)
        assertNull(request.assignmentId)
    }

    @Test
    fun `custom emergency request matches parameters for SELF and CARDIAC`() {
        val request = EmergencyRequest(
            id = "req_123",
            patientId = "patient_abc",
            forWhom = "SELF",
            emergencyType = "CARDIAC",
            latitude = 19.0760,
            longitude = 72.8777,
            status = "REQUESTED"
        )

        assertEquals("req_123", request.id)
        assertEquals("patient_abc", request.patientId)
        assertEquals("SELF", request.forWhom)
        assertEquals("CARDIAC", request.emergencyType)
        assertEquals(19.0760, request.latitude, 0.0001)
        assertEquals(72.8777, request.longitude, 0.0001)
        assertEquals("REQUESTED", request.status)
    }

    @Test
    fun `custom emergency request matches parameters for OTHER and ACCIDENT`() {
        val request = EmergencyRequest(
            id = "req_456",
            patientId = "patient_xyz",
            forWhom = "OTHER",
            emergencyType = "ACCIDENT",
            latitude = 28.6139,
            longitude = 77.2090,
            status = "REQUESTED"
        )

        assertEquals("req_456", request.id)
        assertEquals("OTHER", request.forWhom)
        assertEquals("ACCIDENT", request.emergencyType)
    }
}
