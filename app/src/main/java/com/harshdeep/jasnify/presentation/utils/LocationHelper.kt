package com.harshdeep.jasnify.presentation.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {

    const val LOCATION_PREFS = "jasnify_location_prefs"
    const val RECENT_SEARCHES_KEY = "recent_searches_key"

    fun getRecentLocations(context: Context): List<String> {
        val prefs = context.getSharedPreferences(LOCATION_PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(RECENT_SEARCHES_KEY, null) ?: return emptyList()
        return saved.split("|||").filter { it.isNotBlank() }
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun simplifyAddress(address: Address): String {
        val locality = address.locality ?: address.subAdminArea ?: ""
        val subLocality = address.subLocality ?: ""
        val state = address.adminArea ?: ""

        return when {
            subLocality.isNotEmpty() && locality.isNotEmpty() -> "$subLocality, $locality"
            locality.isNotEmpty() && state.isNotEmpty() -> "$locality, $state"
            locality.isNotEmpty() -> locality
            state.isNotEmpty() -> state
            else -> "Unknown Location"
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAndResolveAddress(
        context: Context,
        coroutineScope: CoroutineScope,
        onAddressResolved: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                resolveAddress(context, coroutineScope, location, onAddressResolved)
            } else {
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setMaxUpdates(1)
                    .build()

                fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val newLoc = locationResult.lastLocation
                        if (newLoc != null) {
                            resolveAddress(context, coroutineScope, newLoc, onAddressResolved)
                        } else {
                            onError("Location not found")
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }, context.mainLooper)
            }
        }.addOnFailureListener {
            onError(it.message ?: "Failed to get location")
        }
    }

    private fun resolveAddress(
        context: Context,
        coroutineScope: CoroutineScope,
        location: Location,
        onAddressResolved: (String) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val simpleAddress = simplifyAddress(addresses[0])
                    withContext(Dispatchers.Main) {
                        onAddressResolved(simpleAddress)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun checkSettingsAndFetchLocation(
        context: Context,
        gpsResolutionLauncher: ActivityResultLauncher<IntentSenderRequest>,
        onSuccess: () -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            onSuccess()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    gpsResolutionLauncher.launch(intentSenderRequest)
                } catch (_: Exception) {}
            }
        }
    }
}
