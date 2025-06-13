package com.sevalk.presentation.customer.home

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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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

    // Sample data
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

    // Initialize map position (Matara, Sri Lanka)
    val defaultLocation = LatLng(5.9549, 80.5550)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Add markers for service providers
            serviceProviders.forEach { provider ->
                if (selectedServiceType == ServiceType.ALL || provider.type == selectedServiceType) {
                    Marker(
                        state = MarkerState(LatLng(provider.latitude, provider.longitude)),
                        title = provider.name,
                        snippet = provider.type.displayName
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

// Preview
@Composable
fun ServiceProviderMapScreenPreview() {
    MaterialTheme {
        ServiceProviderMapScreen()
    }
}