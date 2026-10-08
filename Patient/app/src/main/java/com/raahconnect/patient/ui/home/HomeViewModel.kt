package com.raahconnect.patient.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.raahconnect.patient.data.location.LocationHelper
import com.raahconnect.patient.data.location.LocationState
import com.raahconnect.patient.data.model.EmergencyRequest
import com.raahconnect.patient.data.repository.AuthRepository
import com.raahconnect.patient.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

data class HomeUiState(
    val patientName: String = "",
    val patientEmail: String = "",
    val patientPhone: String = "",
    val isProfileLoading: Boolean = true,
    val profileError: String? = null,
    val locationState: LocationState = LocationState.Idle,
    val isSosDialogVisible: Boolean = false,
    val isCreatingEmergency: Boolean = false,
    val emergencyError: String? = null
)

class HomeViewModel @JvmOverloads constructor(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository(),
    private val emergencyRepository: EmergencyRepository = EmergencyRepository()
) : AndroidViewModel(application) {

    private val locationHelper = LocationHelper(application.applicationContext)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPatientProfile()
        checkLocationStatus()
    }

    fun loadPatientProfile() {
        val uid = authRepository.currentUid
        if (uid == null) {
            _uiState.update {
                it.copy(
                    isProfileLoading = false,
                    profileError = "User session expired."
                )
            }
            return
        }

        _uiState.update { it.copy(isProfileLoading = true, profileError = null) }

        viewModelScope.launch {
            val result = authRepository.getPatientProfile(uid)
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            patientName = user.name.ifBlank { "Patient" },
                            patientEmail = user.email,
                            patientPhone = user.phone,
                            isProfileLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
                            profileError = error.localizedMessage ?: "Failed to load profile",
                            patientName = authRepository.currentUser?.displayName ?: "Patient"
                        )
                    }
                }
            )
        }
    }

    /**
     * Checks permission and starts location detection if permitted.
     */
    fun checkLocationStatus() {
        if (!locationHelper.hasLocationPermission()) {
            _uiState.update { it.copy(locationState = LocationState.PermissionNeeded) }
            return
        }

        if (!locationHelper.isLocationServicesEnabled()) {
            _uiState.update { it.copy(locationState = LocationState.ServicesDisabled) }
            return
        }

        fetchCurrentLocation()
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            checkLocationStatus()
        } else {
            _uiState.update {
                it.copy(
                    locationState = LocationState.PermissionDenied
                )
            }
        }
    }

    fun fetchCurrentLocation() {
        _uiState.update { it.copy(locationState = LocationState.Fetching) }

        viewModelScope.launch {
            val result = locationHelper.getCurrentLocation()
            result.fold(
                onSuccess = { location ->
                    _uiState.update {
                        it.copy(
                            locationState = LocationState.Detected(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            locationState = LocationState.Error(
                                error.message ?: "Unable to detect GPS location. Please check location settings."
                            )
                        )
                    }
                }
            )
        }
    }

    // ----------------------------------------------------
    // SOS & Emergency Handlers
    // ----------------------------------------------------

    fun onSosClicked() {
        // Open the dialog
        _uiState.update {
            it.copy(
                isSosDialogVisible = true,
                emergencyError = null
            )
        }

        // Trigger a fresh location check if not yet detected
        if (_uiState.value.locationState !is LocationState.Detected) {
            checkLocationStatus()
        }
    }

    fun dismissSosDialog() {
        if (!_uiState.value.isCreatingEmergency) {
            _uiState.update {
                it.copy(
                    isSosDialogVisible = false,
                    emergencyError = null
                )
            }
        }
    }

    fun createEmergencyRequest(
        forWhom: String,
        emergencyType: String,
        onSuccess: (EmergencyRequest) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingEmergency = true, emergencyError = null) }

            // Ensure we have current GPS coordinates
            var currentLocation = when (val state = _uiState.value.locationState) {
                is LocationState.Detected -> state
                else -> null
            }

            if (currentLocation == null) {
                // Attempt fresh fetch immediately
                val locResult = locationHelper.getCurrentLocation()
                if (locResult.isSuccess) {
                    val loc = locResult.getOrThrow()
                    currentLocation = LocationState.Detected(loc.latitude, loc.longitude, loc.accuracy)
                    _uiState.update { it.copy(locationState = currentLocation) }
                } else {
                    val errMsg = locResult.exceptionOrNull()?.message
                        ?: "Real GPS coordinates required to create emergency request. Please enable location."
                    _uiState.update {
                        it.copy(
                            isCreatingEmergency = false,
                            emergencyError = errMsg
                        )
                    }
                    return@launch
                }
            }

            val result = emergencyRepository.createEmergencyRequest(
                forWhom = forWhom,
                emergencyType = emergencyType,
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )

            result.fold(
                onSuccess = { request ->
                    _uiState.update {
                        it.copy(
                            isCreatingEmergency = false,
                            isSosDialogVisible = false,
                            emergencyError = null
                        )
                    }
                    onSuccess(request)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isCreatingEmergency = false,
                            emergencyError = error.message ?: "Failed to send emergency request."
                        )
                    }
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }

    companion object {
        fun provideFactory(
            application: Application,
            authRepository: AuthRepository = AuthRepository(),
            emergencyRepository: EmergencyRepository = EmergencyRepository()
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(application, authRepository, emergencyRepository) as T
            }
        }
    }
}
