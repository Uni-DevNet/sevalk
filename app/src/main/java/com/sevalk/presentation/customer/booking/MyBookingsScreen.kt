package com.sevalk.presentation.customer.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sevalk.R
import com.sevalk.ui.theme.SevaLKTheme

data class BookingStatus(
    val name: String,
    val color: Color,
    val backgroundColor: Color
)

data class MyBooking(
    val id: String,
    val serviceName: String,
    val providerName: String,
    val date: String,
    val time: String,
    val status: BookingStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableStateOf(0) }
    
    val filters = listOf("All", "Pending", "Accepted", "Unpaid", "Completed")
    
    val bookingStatuses = mapOf(
        "confirmed" to BookingStatus("Confirmed", Color(0xFF10B981), Color(0xFFD1FAE5)),
        "pending" to BookingStatus("Pending", Color(0xFFF59E0B), Color(0xFFFEF3C7)),
        "rejected" to BookingStatus("Rejected", Color(0xFFEF4444), Color(0xFFFEE2E2))
    )
    
    val allBookings = listOf(
        MyBooking(
            "1",
            "Plumbing repair",
            "Mike's Plumbing",
            "Today",
            "2:00 PM",
            bookingStatuses["confirmed"]!!
        ),
        MyBooking(
            "2", 
            "Math Tutoring",
            "Lisa Chen",
            "Tomorrow",
            "4:00 PM",
            bookingStatuses["pending"]!!
        ),
        MyBooking(
            "3",
            "Plumbing repair", 
            "Mike's Plumbing",
            "Today",
            "2:00 PM",
            bookingStatuses["confirmed"]!!
        ),
        MyBooking(
            "4",
            "Plumbing repair",
            "Elly's Plumbing", 
            "Today",
            "2:00 PM",
            bookingStatuses["rejected"]!!
        )
    )
    
    val filteredBookings = when (selectedFilterIndex) {
        1 -> allBookings.filter { it.status.name == "Pending" }
        2 -> allBookings.filter { it.status.name == "Confirmed" }
        3 -> allBookings // Unpaid filter - you can add logic here
        4 -> allBookings // Completed filter - you can add logic here  
        else -> allBookings
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "My Bookings",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        // Filter Tabs
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters.size) { index ->
                FilterChip(
                    text = filters[index],
                    isSelected = selectedFilterIndex == index,
                    onClick = { selectedFilterIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bookings List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredBookings) { booking ->
                BookingCard(booking = booking)
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFFFC107) else Color(0xFFF5F5F5)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun BookingCard(
    booking: MyBooking,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Navigate to booking details */ },
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
//            // Service Icon
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(Color(0xFFF3F4F6), CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Default.Build,
//                    contentDescription = null,
//                    tint = Color.Gray,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
            
//            Spacer(modifier = Modifier.width(12.dp))
            
            // Booking Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.serviceName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    booking.providerName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        " ${booking.date}, ${booking.time}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = booking.status.backgroundColor
                ) {
                    Text(
                        booking.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = booking.status.color
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action Buttons
                Row {
                    IconButton(
                        onClick = { /* Handle call */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { /* Handle message */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFC107),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.message_circle),
                                contentDescription = "Message",
                                tint = Color.Black,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun MyBookingsScreenPreview() {
    SevaLKTheme {
        MyBookingsScreen(
            navController = NavController(context = LocalContext.current),
            modifier = Modifier.fillMaxSize()
        )
    }
}
