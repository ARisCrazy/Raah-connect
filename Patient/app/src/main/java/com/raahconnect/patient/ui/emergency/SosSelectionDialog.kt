package com.raahconnect.patient.ui.emergency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.raahconnect.patient.ui.theme.EmergencyRed
import com.raahconnect.patient.ui.theme.EmergencyRedDark
import com.raahconnect.patient.ui.theme.EmergencyRedLight
import com.raahconnect.patient.ui.theme.MedicalBlue
import com.raahconnect.patient.ui.theme.NavyDark
import com.raahconnect.patient.ui.theme.TextMuted
import com.raahconnect.patient.ui.theme.TextPrimary
import com.raahconnect.patient.ui.theme.TextSecondary

private enum class SosStep {
    SELECT_RECIPIENT,
    SELECT_TYPE
}

@Composable
fun SosSelectionDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirmEmergency: (forWhom: String, emergencyType: String) -> Unit
) {
    var currentStep by remember { mutableStateOf(SosStep.SELECT_RECIPIENT) }
    var selectedForWhom by remember { mutableStateOf<String?>(null) } // "SELF" or "OTHER"

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = !isLoading, dismissOnClickOutside = !isLoading)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Header Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (currentStep == SosStep.SELECT_TYPE && !isLoading) {
                        IconButton(
                            onClick = { currentStep = SosStep.SELECT_RECIPIENT },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NavyDark
                            )
                        }
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = EmergencyRedLight,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (currentStep == SosStep.SELECT_RECIPIENT) "1/2" else "2/2",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = EmergencyRedDark
                                )
                            }
                        }
                    }

                    Text(
                        text = "EMERGENCY DISPATCH",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = EmergencyRedDark
                    )

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp)
                    ) {
                        CircularProgressIndicator(
                            color = EmergencyRed,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Sending Emergency Alert...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Registering request with GPS coordinates",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                } else {
                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmergencyRedLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = EmergencyRedDark,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    when (currentStep) {
                        SosStep.SELECT_RECIPIENT -> {
                            Text(
                                text = "Who needs emergency assistance?",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select whether this request is for you or someone nearby",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            SosOptionCard(
                                title = "MYSELF",
                                subtitle = "I require urgent medical assistance",
                                icon = Icons.Default.Person,
                                iconTint = EmergencyRed,
                                onClick = {
                                    selectedForWhom = "SELF"
                                    currentStep = SosStep.SELECT_TYPE
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            SosOptionCard(
                                title = "SOMEONE ELSE",
                                subtitle = "Reporting on behalf of another patient",
                                icon = Icons.Default.Group,
                                iconTint = MedicalBlue,
                                onClick = {
                                    selectedForWhom = "OTHER"
                                    currentStep = SosStep.SELECT_TYPE
                                }
                            )
                        }

                        SosStep.SELECT_TYPE -> {
                            Text(
                                text = "What is the emergency?",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choose the nature of the medical emergency",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            SosOptionCard(
                                title = "❤️ CARDIAC",
                                subtitle = "Severe chest pain, heart attack, or shortness of breath",
                                icon = Icons.Default.Favorite,
                                iconTint = EmergencyRed,
                                onClick = {
                                    val forWhom = selectedForWhom ?: "SELF"
                                    onConfirmEmergency(forWhom, "CARDIAC")
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            SosOptionCard(
                                title = "🚗 ACCIDENT",
                                subtitle = "Road accident, trauma, injury, or collision",
                                icon = Icons.Default.DirectionsCar,
                                iconTint = Color(0xFFD97706),
                                onClick = {
                                    val forWhom = selectedForWhom ?: "SELF"
                                    onConfirmEmergency(forWhom, "ACCIDENT")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SosOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = NavyDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
