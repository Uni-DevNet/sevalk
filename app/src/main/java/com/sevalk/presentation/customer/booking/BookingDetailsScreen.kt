package com.sevalk.presentation.customer.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sevalk.R
import com.sevalk.ui.theme.SevaLKTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    navController: NavController,
    bookingId: String,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Service Unpaid Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.message_circle), // Use appropriate icon
                        contentDescription = null,
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Service Unpaid",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        "Payment is pending for the completed service",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Provider Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Mike's Plumbing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        "Plumbing",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "4.8",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "(127)",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = { /* Handle call */ }) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.Gray)
                }

                IconButton(onClick = { /* Handle message */ }) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.message_circle),
                            contentDescription = "Message",
                            tint = Color.Black,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Service Details
            Text(
                "Service Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            ServiceDetailRow("Service", "Plumbing Repair")
            ServiceDetailRow("Duration", "2.5 hours")
            ServiceDetailRow("Date & Time", "Today, 2:00 PM - 4:30 PM")
            ServiceDetailRow("Location", "123 Main Street, Weligama")

            Spacer(modifier = Modifier.height(32.dp))

            // Payment Summary
            Text(
                "Payment Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentRow("Labor", "LKR 1250")
            PaymentRow("Materials", "LKR 350")
            PaymentRow("Platform Fee", "LKR 48")

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(8.dp))

            PaymentRow("Total", "LKR 1648", isTotal = true)

            Spacer(modifier = Modifier.height(32.dp))

            // Rating Section
            Text(
                "Rate this service",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < rating) Color(0xFFFFC107) else Color(0xFFE5E7EB),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { rating = index + 1 }
                            .padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Share your experience (Optional)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE5E7EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Proceed to Payment Button
            Button(
                onClick = { 
                    navController.navigate("payment/$bookingId")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.message_circle), // Use credit card icon
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Proceed to Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ServiceDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentRow(
    label: String,
    amount: String,
    isTotal: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = if (isTotal) 18.sp else 16.sp,
            color = Color.Black,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            amount,
            fontSize = if (isTotal) 18.sp else 16.sp,
            color = Color.Black,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun BookingDetailsScreenPreview() {
    SevaLKTheme {
        BookingDetailsScreen(
            navController = NavController(context = LocalContext.current),
            bookingId = "1"
        )
    }
}

