package com.sevalk.presentation.customer.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.sevalk.R
import kotlin.math.roundToInt


data class ServiceProvider(
    val id: String,
    val name: String,
    val type: ServiceType,
    val latitude: Double,
    val longitude: Double,
    val rating: Float = 0f
)

enum class ServiceType(val displayName: String, val icon: ImageVector, val color: Color) {
    ALL("All", Icons.Default.AccountBox, Color(0xFFFFC107)),
    PLUMBING("Plumbing", Icons.Default.Build, Color(0xFF2196F3)),
    ELECTRICAL("Electrical", Icons.Default.Add, Color(0xFF4CAF50)),
    CLEANING("Cleaning", Icons.Default.Call, Color(0xFF9C27B0))
}

@Composable
fun ServiceProviderMapScreen() {
    var selectedServiceType by remember { mutableStateOf(ServiceType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request location permissions
    LaunchedEffect(Unit) {
        requestLocationPermission(context)
        getCurrentLocation(context, fusedLocationClient) { location ->
            currentLocation = location
        }
    }

    // Sample data - now including distance calculation from current location
    val serviceProviders = remember {
        listOf(
            ServiceProvider("1", "Matara Central College", ServiceType.PLUMBING, 5.9485, 80.5353),
            ServiceProvider("2", "Department of Immigration", ServiceType.ELECTRICAL, 5.9475, 80.5343),
            ServiceProvider("3", "Sri Darmawansa Mawatha", ServiceType.CLEANING, 5.9495, 80.5363),
            ServiceProvider("4", "Dr. S.A. Wickremasinghe Mawatha", ServiceType.PLUMBING, 5.9465, 80.5333),
            ServiceProvider("5", "Samanmal - Matara", ServiceType.ELECTRICAL, 5.9455, 80.5323),
            ServiceProvider("6", "Cargills Food City - Matara 1", ServiceType.CLEANING, 5.9445, 80.5313),
            ServiceProvider("7", "Weligama Football Stadium", ServiceType.ALL, 5.9435, 80.5303)
        )
    }

    // Initialize map position with current location or default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation ?: LatLng(5.9549, 80.5550), // Default to Matara if no location
            14f
        )
    }

    // Update camera when location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 14f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
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
                        icon = BitmapDescriptorFactory.fromResource(iconResId)
                    )
                }
            }
        }

        // Top status bar
        StatusBar(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .align(Alignment.TopStart)
        )

        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopStart)
                .offset(y = 60.dp)
                .zIndex(1f)
        )

        // Service type filters
        ServiceTypeFilters(
            selectedType = selectedServiceType,
            onTypeSelected = { selectedServiceType = it },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .offset(y = 120.dp)
                .zIndex(1f)
        )

        // Bottom navigation
        BottomNavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .zIndex(1f)
        )
    }
}

@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Signal bars
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height((8 + index * 2).dp)
                        .background(Color.Black, RoundedCornerShape(1.dp))
                )
            }

            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "WiFi",
                modifier = Modifier.size(16.dp)
            )

            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Battery",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search providers...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(25.dp))
            .background(Color.White, RoundedCornerShape(25.dp)),
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun ServiceTypeFilters(
    selectedType: ServiceType,
    onTypeSelected: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ServiceType.values()) { type ->
            ServiceTypeChip(
                type = type,
                isSelected = type == selectedType,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
fun ServiceTypeChip(
    type: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) type.color else Color.White
    val contentColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = type.displayName,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ServiceMarker(
    provider: ServiceProvider,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .shadow(4.dp, CircleShape)
            .background(provider.type.color, CircleShape)
            .clickable { /* Handle marker click */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = provider.type.icon,
            contentDescription = provider.name,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, false),
            Triple("Search", Icons.Default.Search, true),
            Triple("Bookings", Icons.Default.Edit, false),
            Triple("Messages", Icons.Default.Home, false),
            Triple("Profile", Icons.Default.Person, false)
        )

        items.forEach { (title, icon, isSelected) ->
            NavigationBarItem(
                selected = isSelected,
                onClick = { /* Handle navigation */ },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFC107),
                    selectedTextColor = Color(0xFFFFC107),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

// Location permission and tracking
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
                    }
                }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Helper function to calculate distance between two points
private fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}

// Preview
@Composable
fun ServiceProviderMapScreenPreview() {
    MaterialTheme {
        ServiceProviderMapScreen()
    }
}