package com.sevalk.presentation.provider.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sevalk.presentation.navigation.Screen

data class Job(
    val title: String,
    val customer: String,
    val time: String,
    val status: String,
    val icon: ImageVector? = null,
    val iconText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderHomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(1) } // 1 = My Business selected by default
    val upcomingJobs = listOf(
        Job("Plumbing repair", "Customer: John Doe", "Tomorrow, 1:00 PM", "Pending", Icons.Default.Build),
        Job("Math Tutoring", "Customer: Jane Doe", "Tomorrow, 1:00 PM", "Pending", iconText = "\uD83D\uDCDA")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
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
                    Text(
                        "\uD83D\uDCCD Weligama, Southern Province",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                Row {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Handle profile */ }) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Gray
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Tab-like Buttons Row
            Row(modifier = Modifier.fillMaxWidth()) {
                com.sevalk.presentation.components.common.PrimaryButton(
                    text = "Find Services",
                    onClick = { 
                        navController.navigate(Screen.Home.route) {
                            // Clear back stack to prevent going back to provider home
                            popUpTo(Screen.ProviderHome.route) { inclusive = false }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    style = if (selectedTab == 0) com.sevalk.presentation.components.common.PrimaryButtonStyle.TEXT else com.sevalk.presentation.components.common.PrimaryButtonStyle.OUTLINE,
                    backgroundColor = if (selectedTab == 0) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.2f),
                    foregroundColor = if (selectedTab == 0) Color.Black else Color.Gray.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                com.sevalk.presentation.components.common.PrimaryButton(
                    text = "My Business",
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f),
                    style = if (selectedTab == 1) com.sevalk.presentation.components.common.PrimaryButtonStyle.TEXT else com.sevalk.presentation.components.common.PrimaryButtonStyle.OUTLINE,
                    backgroundColor = if (selectedTab == 1) Color(0xFFFFC107) else Color(0xFFF5F5F5),
                    foregroundColor = if (selectedTab == 1) Color.White else Color(0xFFFFC107)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Today's Overview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Today's Overview",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("3", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("Today's Bookings", color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LKR 25,250", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("This Week", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            // Upcoming Jobs Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Upcoming Jobs",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                TextButton(onClick = { /* View all */ }) {
                    Text("View all", color = Color(0xFFFFC107), fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Job Cards
        items(upcomingJobs) { job ->
            JobCard(job = job)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            
            // Your Performance
            Text(
                "Your Performance",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Performance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "4.8",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                            Text(
                                "⭐⭐⭐⭐⭐",
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            "57 complete jobs",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = 0.8f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFFC107),
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}

@Composable
fun JobCard(job: Job) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Job Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (job.icon != null) {
                    Icon(
                        job.icon,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                } else if (job.iconText != null) {
                    Text(
                        job.iconText,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Fallback icon
                    Text(
                        "🔧",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Job Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    job.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    job.customer,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    job.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            // Status Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF3CD)
            ) {
                Text(
                    job.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color(0xFFB8860B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}