package com.raahconnect.patient.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Home : NavRoutes("home")
    object Confirmation : NavRoutes("confirmation/{emergencyId}/{emergencyType}/{forWhom}/{lat}/{lng}") {
        fun createRoute(
            emergencyId: String,
            emergencyType: String,
            forWhom: String,
            latitude: Double,
            longitude: Double
        ): String {
            val encodedId = URLEncoder.encode(emergencyId, StandardCharsets.UTF_8.toString())
            return "confirmation/$encodedId/$emergencyType/$forWhom/$latitude/$longitude"
        }
    }
}
