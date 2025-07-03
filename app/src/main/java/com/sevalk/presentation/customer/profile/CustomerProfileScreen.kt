package com.sevalk.presentation.customer.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import for remember and mutableStateOf
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
import com.sevalk.R
import androidx.navigation.NavController
import com.sevalk.presentation.navigation.Screen

// Data class to hold profile information
data class UserProfile(
    val name: String,
    val memberSince: String, // e.g., "March 2023"
    val location: String, // e.g., "Weligama, Southern Province"
    val totalBookings: Int,
    val completedBookings: Int,
    val rating: Double,
    val email: String,
    val phoneNumber: String,
    val joinDate: String // e.g., "March 2023" (just the month/year of joining)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    navController: NavController,
    initialUserProfile: UserProfile, // Changed to initialUserProfile to manage mutable state internally
    onSwitchToProviderClick: () -> Unit,
    // onEditProfileClick will now be handled internally to show the popup
    onLogoutClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onHelpSupportClick: () -> Unit
) {
    // State to hold the current user profile, so it can be updated by the popup
    var userProfile by remember { mutableStateOf(initialUserProfile) }

    // State to control the visibility of the edit profile popup
    var showEditProfilePopup by remember { mutableStateOf(false) }

    // Define consistent colors
    val YellowHighlight = Color(0xFFFDD835)
    val CardBackground = Color(0xFFF8F9FA) // Light gray background for cards

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Profile Header Section
            Button(
                onClick = onSwitchToProviderClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowHighlight,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                modifier = Modifier.padding(end = 8.dp)
                    .align(Alignment.End)
            ) {
                Text("Switch to Provider", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Profile Picture
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    // Edit icon for profile picture - this will open the popup
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(YellowHighlight)
                            .border(1.dp, Color.White, CircleShape)
                            .clickable(onClick = { showEditProfilePopup = true }) // Open popup on click
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.camera), // Use a camera icon here or Icons.Default.Edit
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
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
                            imageVector = Icons.Default.LocationOn,
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
                // Edit profile icon - this will also open the popup
                IconButton(
                    onClick = { showEditProfilePopup = true }, // Open popup on click
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Your Activity Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Information Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                    // Changed label back to "Join Date" for consistency with userProfile.joinDate
                    ContactInfoItem(icon = Icons.Default.CalendarToday, label = "Join Date", value = "Member since ${userProfile.joinDate}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Items Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column {
                    NavigationItem(
                        icon = Icons.Default.FavoriteBorder,
                        label = "Favorites",
                        description = "Your saved services",
                        onClick = onFavoritesClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
                    NavigationItem(
                        icon = Icons.Default.Payments,
                        label = "Payment Methods",
                        description = "Manage cards & payments",
                        onClick = onPaymentMethodsClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
                    NavigationItem(
                        icon = Icons.Default.Lock,
                        label = "Privacy & Security",
                        description = "Account protection",
                        onClick = onPrivacySecurityClick
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
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
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red
                ),
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Edit Profile Popup Integration ---
        if (showEditProfilePopup) {
            EditProfilePopup(
                userProfile = userProfile, // Pass the current userProfile data to the popup
                onDismiss = { showEditProfilePopup = false }, // Hide popup on dismiss
                onSave = { newName, newPhoneNumber ->
                    // Update the userProfile state with the new values
                    userProfile = userProfile.copy(
                        name = newName,
                        phoneNumber = newPhoneNumber
                    )
                    showEditProfilePopup = false // Hide popup after saving
                    // TODO: In a real app, you would also trigger an API call to save these changes
                    // to your backend or data layer.
                }
            )
        }
    }
}

@Composable
fun ActivityStat(label: String, value: String, highlight: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlight) Color(0xFFFDD835) else Color.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
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
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerProfileScreenPreview() {
    MaterialTheme {
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
        // CustomerProfileScreen with dummy NavController for preview
    }
}