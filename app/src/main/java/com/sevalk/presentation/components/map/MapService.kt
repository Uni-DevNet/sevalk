package com.sevalk.presentation.components.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.sevalk.R

data class ServiceProvider(
    val id: String,
    val name: String,
    val type: ServiceType,
    val latitude: Double,
    val longitude: Double,
    val rating: Float = 0f,
    val description: String = "",
    val phone: String = "",
    val address: String = ""
)

enum class ServiceType(val displayName: String, val icon: ImageVector, val color: Color) {
    ALL("All", Icons.Default.AccountBox, Color(0xFFFFC107)),
    PLUMBING("Plumbing", Icons.Default.Build, Color(0xFF2196F3)),
    ELECTRICAL("Electrical", Icons.Default.Add, Color(0xFF4CAF50)),
    CLEANING("Cleaning", Icons.Default.Call, Color(0xFF9C27B0))
}

@Composable
fun MapService(
    serviceProviders: List<ServiceProvider> = emptyList(),
    selectedServiceType: ServiceType = ServiceType.ALL,
    showCurrentLocation: Boolean = true,
    onMarkerClick: ((ServiceProvider) -> Unit)? = null,
    modifier: Modifier = Modifier,
    initialZoom: Float = 14f
) {
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        if (showCurrentLocation) {
            requestLocationPermission(context)
            getCurrentLocation(context, fusedLocationClient) { location ->
                currentLocation = location
            }
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, initialZoom)
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(myLocationButtonEnabled = showCurrentLocation),
        properties = MapProperties(
            isMyLocationEnabled = showCurrentLocation && currentLocation != null
        )
    ) {
        serviceProviders.forEach { provider ->
            if (selectedServiceType == ServiceType.ALL || provider.type == selectedServiceType) {
                val iconResId = when (provider.type) {
                    ServiceType.PLUMBING -> R.drawable.map4_24
                    ServiceType.ELECTRICAL -> R.drawable.map3_24
                    ServiceType.CLEANING -> R.drawable.map1_24
                    ServiceType.ALL -> R.drawable.map2_24
                }

                Marker(
                    state = MarkerState(LatLng(provider.latitude, provider.longitude)),
                    title = provider.name,
                    snippet = provider.type.displayName,
                    icon = BitmapDescriptorFactory.fromResource(iconResId),
                    onClick = {
                        onMarkerClick?.invoke(provider)
                        false
                    }
                )
            }
        }
    }
}

private fun requestLocationPermission(context: Context) {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as android.app.Activity,
            permissions,
            1
        )
    }
}

private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationResult: (LatLng) -> Unit
) {
    try {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        onLocationResult(LatLng(it.latitude, it.longitude))
                    } ?: run {
                        fusedLocationClient.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        ).addOnSuccessListener { freshLocation ->
                            freshLocation?.let {
                                onLocationResult(LatLng(it.latitude, it.longitude))
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}
