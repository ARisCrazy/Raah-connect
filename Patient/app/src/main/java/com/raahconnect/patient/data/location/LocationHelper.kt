package com.raahconnect.patient.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.raahconnect.patient.data.repository.awaitResult

sealed class LocationState {
    object Idle : LocationState()
    object PermissionNeeded : LocationState()
    object PermissionDenied : LocationState()
    object ServicesDisabled : LocationState()
    object Fetching : LocationState()
    data class Detected(val latitude: Double, val longitude: Double, val accuracy: Float = 0f) : LocationState()
    data class Error(val message: String) : LocationState()
}

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    fun isLocationServicesEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location> {
        if (!hasLocationPermission()) {
            return Result.failure(SecurityException("Location permission is required to detect emergency location."))
        }

        if (!isLocationServicesEnabled()) {
            return Result.failure(IllegalStateException("Device location services are disabled. Please turn on GPS."))
        }

        return try {
            // Check last known location first for immediate availability
            val lastKnown: Location? = fusedLocationClient.lastLocation.awaitResult()
            if (lastKnown != null && (System.currentTimeMillis() - lastKnown.time < 120_000)) {
                return Result.success(lastKnown)
            }

            // Fresh high-accuracy location fix
            val cancellationTokenSource = CancellationTokenSource()
            val freshLocation: Location? = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).awaitResult()

            if (freshLocation != null) {
                Result.success(freshLocation)
            } else if (lastKnown != null) {
                Result.success(lastKnown)
            } else {
                Result.failure(Exception("Unable to acquire GPS fix. Please ensure location is enabled and you have a clear sky view."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Location detection failed: ${e.localizedMessage ?: "Unknown error"}", e))
        }
    }
}
