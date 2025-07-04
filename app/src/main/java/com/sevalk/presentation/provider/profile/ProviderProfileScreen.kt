package com.sevalk.presentation.provider.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.R
import com.sevalk.presentation.customer.profile.EditProfilePopup
import com.sevalk.presentation.customer.profile.UserProfile
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class ProviderProfile(
    val name: String,
    val memberSince: String,
    val completedJobs: Int,
    val totalJobs: Int,
    val location: String,
    val totalEarnings: String,
    val email: String,
    val phoneNumber: String,
    val isAvailable: Boolean = true,
    val responseTime: String = "within 1 hour"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    initialProviderProfile: ProviderProfile,
    navController: NavController,
    onLogoutClick: () -> Unit,
    onServicesClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onSwitchToCustomerClick: () -> Unit
) {
    var providerProfile by remember { mutableStateOf(initialProviderProfile) }
    var showEditProfilePopup by remember { mutableStateOf(false) }
    val YellowHighlight = Color(0xFFFDD835)
    val CardBackground = Color(0xFFF8F9FA)

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Button(
                    onClick = onSwitchToCustomerClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowHighlight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                        .align(Alignment.End)
                ) {
                    Text("Switch to Customer", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                // Profile Header Section
                Spacer(modifier = Modifier.height(16.dp))
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
                        // Edit icon for profile picture
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(YellowHighlight)
                                .border(1.dp, Color.White, CircleShape)
                                .clickable { showEditProfilePopup = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
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
                            text = providerProfile.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Member since ${providerProfile.memberSince}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "${providerProfile.completedJobs} / ${providerProfile.totalJobs} jobs completed",
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
                                text = providerProfile.location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                    // Edit profile icon
                    IconButton(
                        onClick = { showEditProfilePopup = true },
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

                // Business Overview Section
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
                            text = "Business Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BusinessStat(label = "Total Jobs", value = providerProfile.totalJobs.toString())
                            BusinessStat(label = "Total Earnings", value = providerProfile.totalEarnings)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Canvas(modifier = Modifier.size(8.dp)) {
                                    drawCircle(
                                        color = if (providerProfile.isAvailable) Color(0xFF4CAF50) else Color(0xFFFF5252)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (providerProfile.isAvailable) "Available" else "Unavailable",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (providerProfile.isAvailable) Color(0xFF4CAF50) else Color(0xFFFF5252)
                                )
                            }
                            Text(
                                text = "Responses in ${providerProfile.responseTime}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
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
                        ContactInfoItem(icon = Icons.Default.Email, label = "Email Address", value = providerProfile.email)
                        ContactInfoItem(icon = Icons.Default.Phone, label = "Phone Number", value = providerProfile.phoneNumber)
                        ContactInfoItem(icon = Icons.Default.CalendarToday, label = "Join Date", value = "Member since ${providerProfile.memberSince}")
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
                            icon = R.drawable.wrench,
                            label = "My Services",
                            description = "Manage service offerings",
                            onClick = onServicesClick
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
                        NavigationItem(
                            icon = R.drawable.credit_card,
                            label = "Payment Methods",
                            description = "Manage costs & payments",
                            onClick = onPaymentMethodsClick
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
                        NavigationItem(
                            icon = R.drawable.shield,
                            label = "Privacy & Security",
                            description = "Account personnel",
                            onClick = onPrivacySecurityClick
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 1.dp)
                        NavigationItem(
                            icon = R.drawable.circle_help,
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
                            painter = painterResource(id = R.drawable.log_out),
                            contentDescription = "Log Out",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Red
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

            // Edit Profile Popup
            if (showEditProfilePopup) {
                EditProfilePopup(
                    userProfile = UserProfile(
                        name = providerProfile.name,
                        phoneNumber = providerProfile.phoneNumber,
                        // Provide default values for other fields
                        memberSince = providerProfile.memberSince,
                        location = providerProfile.location,
                        totalBookings = 0,
                        completedBookings = 0,
                        rating = 0.0,
                        email = providerProfile.email,
                        joinDate = providerProfile.memberSince
                    ),
                    onDismiss = { showEditProfilePopup = false },
                    onSave = { newName, newPhoneNumber ->
                        providerProfile = providerProfile.copy(
                            name = newName,
                            phoneNumber = newPhoneNumber
                        )
                        showEditProfilePopup = false
                    }
                )
            }
        }
    }
}

@Composable
fun BusinessStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
fun NavigationItem(icon: Int, label: String, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
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
fun ProviderProfileScreenPreview() {
    MaterialTheme {
        val sampleProfile = ProviderProfile(
            name = "John Plumbing",
            memberSince = "March 2023",
            completedJobs = 43,
            totalJobs = 327,
            location = "Weligama, Southern Province",
            totalEarnings = "LKR 45,600",
            email = "john.obus@email.com",
            phoneNumber = "+94 77 123 4567",
            isAvailable = true,
            responseTime = "1 hour"
        )
        ProviderProfileScreen(
            initialProviderProfile = sampleProfile,
            navController = rememberNavController(),
            onLogoutClick = {},
            onServicesClick = {},
            onPaymentMethodsClick = {},
            onPrivacySecurityClick = {},
            onHelpSupportClick = {},
            onSwitchToCustomerClick = {}
        )
    }
}