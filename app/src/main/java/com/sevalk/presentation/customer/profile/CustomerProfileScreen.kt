package com.sevalk.presentation.customer.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.* // Using Material 3 components
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.R // Assuming you have drawable resources like profile_placeholder and possibly colors

// Define a data class to hold profile information
data class UserProfile(
    val name: String,
    val memberSince: String,
    val location: String,
    val totalBookings: Int,
    val completedBookings: Int,
    val rating: Double,
    val email: String,
    val phoneNumber: String,
    val joinDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    userProfile: UserProfile,
    onSwitchToProviderClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onHelpSupportClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title in the image's top app bar, customize as needed */ },
                actions = {
                    // This section is for the "Switch to Provider" button
                    Button(
                        onClick = onSwitchToProviderClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDD835), // A yellow color
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Switch to Provider", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Spacer to push button slightly left
                },
                // Customize colors to match the app's overall theme, typically transparent or a light color
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black // Adjust as needed
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Header Section
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Picture (Placeholder)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray) // Placeholder background
                        .border(1.dp, Color.Gray, CircleShape) // Optional border
                ) {
                    Image(
                        // Replace with your actual profile image resource
                        // For a placeholder, you might use a generic icon or a simple background
                        painter = painterResource(id = R.drawable.camera), // Assume you have this drawable
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                    // Camera icon for changing profile picture (positioned bottom right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp) // Adjust offset to position icon correctly
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary) // Or a specific color for the icon background
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit, // Or a camera icon if you have one
                            contentDescription = "Change Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Member since ${userProfile.memberSince}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn, // Assuming a location icon
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userProfile.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onEditProfileClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Your Activity Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ActivityStat(label = "Total Bookings", value = userProfile.totalBookings.toString())
                        ActivityStat(label = "Completed", value = userProfile.completedBookings.toString())
                        ActivityStat(label = "Rating", value = userProfile.rating.toString(), highlight = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Information Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactInfoItem(icon = Icons.Default.MailOutline, label = "Email Address", value = userProfile.email)
                    ContactInfoItem(icon = Icons.Default.Phone, label = "Phone Number", value = userProfile.phoneNumber)
                    ContactInfoItem(icon = Icons.Default.CalendarToday, label = "Join Date", value = "Member since ${userProfile.joinDate}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Items Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    NavigationItem(
                        icon = Icons.Default.FavoriteBorder,
                        label = "Favorites",
                        description = "Your saved services",
                        onClick = onFavoritesClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    NavigationItem(
                        icon = Icons.Default.Payments,
                        label = "Payment Methods",
                        description = "Manage cards & payments",
                        onClick = onPaymentMethodsClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    NavigationItem(
                        icon = Icons.Default.Lock,
                        label = "Privacy & Security",
                        description = "Account protection",
                        onClick = onPrivacySecurityClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    NavigationItem(
                        icon = Icons.Default.HelpOutline,
                        label = "Help & Support",
                        description = "Get assistance",
                        onClick = onHelpSupportClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log Out Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // White background
                    contentColor = Color.Red // Red text
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Log Out", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ActivityStat(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color(0xFFFDD835) else Color.Black // Yellow for rating
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NavigationItem(icon: ImageVector, label: String, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Navigate", tint = Color.Gray, modifier = Modifier.size(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerProfileScreenPreview() {
    // You'll need to define your app's theme for the preview to look correct
    // For example: MyAppTheme { ... }
    MaterialTheme { // Using MaterialTheme directly for simple preview
        val sampleUserProfile = UserProfile(
            name = "John Smith",
            memberSince = "March 2023",
            location = "Weligama, Southern Province",
            totalBookings = 24,
            completedBookings = 10,
            rating = 4.8,
            email = "john.doe@email.com",
            phoneNumber = "+94 77 123 4567",
            joinDate = "March 2023"
        )
        CustomerProfileScreen(
            userProfile = sampleUserProfile,
            onSwitchToProviderClick = { /*TODO*/ },
            onEditProfileClick = { /*TODO*/ },
            onLogoutClick = { /*TODO*/ },
            onFavoritesClick = { /*TODO*/ },
            onPaymentMethodsClick = { /*TODO*/ },
            onPrivacySecurityClick = { /*TODO*/ },
            onHelpSupportClick = { /*TODO*/ }
        )
    }
}