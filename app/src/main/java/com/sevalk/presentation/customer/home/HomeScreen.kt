package com.sevalk.presentation.customer.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.presentation.auth.components.CustomTextField
import com.sevalk.presentation.components.common.PrimaryButton
import com.sevalk.presentation.components.common.PrimaryButtonStyle
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.S_LIGHT_TEXT
import com.sevalk.ui.theme.S_INPUT_BACKGROUND
import androidx.navigation.NavController
import com.sevalk.presentation.navigation.Screen
import com.sevalk.ui.theme.SevaLKTheme

data class ServiceItem(
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

data class BookingItem(
    val serviceName: String,
    val providerName: String,
    val date: String,
    val time: String,
    val status: String,
    val statusColor: Color
)

data class ProviderItem(
    val name: String,
    val service: String,
    val rating: Float,
    val distance: String,
    val availability: String,
    val availabilityColor: Color
)

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Sample data with only confirmed available Material Icons
    val popularServices = listOf(
        ServiceItem("Plumbing", Icons.Default.Build, Color(0xFF6366F1)),
        ServiceItem("Electrical", Icons.Default.Settings, Color(0xFFF59E0B)), // Using Settings icon
        ServiceItem("Cleaning", Icons.Default.Home, Color(0xFF10B981)),
        ServiceItem("Auto Repair", Icons.Default.Build, Color(0xFFEF4444)), // Using Build icon
        ServiceItem("Tutoring", Icons.Default.Person, Color(0xFF8B5CF6)), // Using Person icon
        ServiceItem("Beauty", Icons.Default.Face, Color(0xFFEC4899))
    )

    val bookings = listOf(
        BookingItem(
            "Plumbing repair",
            "Mike's Plumbing",
            "Today",
            "2:00 PM - 4:00 PM",
            "pending",
            Color(0xFFF59E0B)
        ),
        BookingItem(
            "Math Tutoring",
            "Ian Chen",
            "Tomorrow",
            "3:00 PM - 4:00 PM",
            "pending",
            Color(0xFFF59E0B)
        )
    )

    val providers = listOf(
        ProviderItem(
            "Mike's Plumbing",
            "Plumbing",
            4.8f,
            "2.3km",
            "Available",
            Color(0xFF10B981)
        ),
        ProviderItem(
            "Sarah Electronics",
            "Electrical",
            4.4f,
            "1.5km",
            "Available",
            Color(0xFF10B981)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Good Morning!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍 ", fontSize = 14.sp)
                    Text(
                        "Weligama, Southern Province",
                        color = S_LIGHT_TEXT,
                        fontSize = 14.sp
                    )
                }
            }

            IconButton(onClick = { /* TODO: Notifications */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Gray
                )
            }

            IconButton(onClick = { /* TODO: Profile */ }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Find Services & My Business buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = "Find Services",
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f),
                style = PrimaryButtonStyle.TEXT
            )
            Spacer(modifier = Modifier.width(8.dp))
            PrimaryButton(
                text = "My Business",
                onClick = {
                    navController.navigate(Screen.ProviderHome.route)
                },
                modifier = Modifier.weight(1f),
                style = PrimaryButtonStyle.OUTLINE,
                backgroundColor = Color.Gray.copy(alpha = 0.4f),
                foregroundColor = S_YELLOW
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search services...",
            backgroundColor = S_INPUT_BACKGROUND,
            focusedBackgroundColor = S_INPUT_BACKGROUND,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Popular Services
        Text(
            "Popular Services",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(popularServices) { service ->
                ServiceCard(service = service)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Your Bookings
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Bookings",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                "View All",
                color = S_YELLOW,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        bookings.forEach { booking ->
            BookingCard(
                booking = booking,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nearby Providers
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Nearby Providers",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                "See All",
                color = S_YELLOW,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        providers.forEach { provider ->
            ProviderCard(
                provider = provider,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ServiceCard(
    service: ServiceItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(width = 100.dp, height = 70.dp)
            .clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(service.backgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    service.icon,
                    contentDescription = service.name,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                service.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF3F4F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.serviceName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    booking.providerName,
                    fontSize = 12.sp,
                    color = S_LIGHT_TEXT
                )
                Text(
                    "${booking.date} • ${booking.time}",
                    fontSize = 12.sp,
                    color = S_LIGHT_TEXT
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = booking.statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    booking.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = booking.statusColor
                )
            }
        }
    }
}

@Composable
fun ProviderCard(
    provider: ProviderItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { /* TODO */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF3F4F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    provider.name.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    provider.service,
                    fontSize = 12.sp,
                    color = S_LIGHT_TEXT
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        " ${provider.rating} (${(provider.rating * 20).toInt()}) • ${provider.distance}",
                        fontSize = 12.sp,
                        color = S_LIGHT_TEXT
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = provider.availabilityColor.copy(alpha = 0.1f)
            ) {
                Text(
                    provider.availability,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = provider.availabilityColor
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    SevaLKTheme {
        HomeScreen(navController = NavController(context = LocalContext.current))
    }
}