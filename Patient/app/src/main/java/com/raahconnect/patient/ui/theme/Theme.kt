package com.raahconnect.patient.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedLight,
    onPrimaryContainer = EmergencyRedDark,
    secondary = MedicalBlue,
    onSecondary = Color.White,
    background = NeutralBackground,
    surface = NeutralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = EmergencyRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedLight,
    onPrimaryContainer = EmergencyRedDark,
    secondary = MedicalBlue,
    onSecondary = Color.White,
    background = NeutralBackground,
    surface = NeutralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = EmergencyRed,
    onError = Color.White
)

@Composable
fun RAAHconnectPatientTheme(
    darkTheme: Boolean = false, // Patient Emergency app uses consistent high-visibility medical theme across all devices
    dynamicColor: Boolean = false, // Keep consistent branding across devices
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}