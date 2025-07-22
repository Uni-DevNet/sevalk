package com.sevalk.presentation.provider.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.data.models.Booking
import com.sevalk.data.models.BookingStatus
import com.sevalk.data.models.toJobDate
import com.sevalk.data.models.toJobTitle
import com.sevalk.ui.theme.S_YELLOW

data class ServiceItem(
    val name: String,
    val rate: String,
    val unit: String,
    var hoursWorked: String = "",
    var squareFeet: String = ""
)

data class AdditionalCost(
    val name: String,
    val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceBillScreen(
    booking: Booking,
    onBackClick: () -> Unit,
    onConfirmBill: () -> Unit
) {
    var serviceItems by remember {
        mutableStateOf(
            listOf(
                ServiceItem("Plumbing", "LKR 300 hourly", "Hours worked"),
                ServiceItem("Electrical Work", "LKR 450 hourly", "Hours worked"),
                ServiceItem("Painting & Decorating", "LKR 100 price per sq ft", "Square Feet")
            )
        )
    }
    
    var additionalCosts by remember {
        mutableStateOf(
            listOf(
                AdditionalCost("Copper pipes (3ft)", 500.0),
                AdditionalCost("Pipe fittings", 200.0)
            )
        )
    }
    
    var notes by remember { mutableStateOf("") }
    
    val subtotal = 6500.0 // Calculate based on service items
    val platformFee = 150.0
    val total = subtotal + platformFee
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Create Service Bill", fontWeight = FontWeight.SemiBold, fontSize = 24.sp) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Job details header
            item {
                Column {
                    Text(
                        text = "Job Title: ${booking.toJobTitle()}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Customer: ${booking.customerName}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Date: ${booking.toJobDate()}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Service Details section
            item {
                Text(
                    text = "Service Details",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(serviceItems) { item ->
                ServiceItemCard(
                    serviceItem = item,
                    onValueChange = { updatedItem ->
                        serviceItems = serviceItems.map { 
                            if (it.name == item.name) updatedItem else it 
                        }
                    }
                )
            }
            
            // Additional Costs section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Additional Costs",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Button(
                        onClick = { /* Add cost logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = S_YELLOW),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Cost", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            
            items(additionalCosts) { cost ->
                AdditionalCostItem(
                    cost = cost,
                    onRemove = {
                        additionalCosts = additionalCosts.filter { it != cost }
                    }
                )
            }
            
            // Additional Total
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Additional Total",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "LKR 700.00",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            
            // Notes section
            item {
                Text(
                    text = "Notes (Optional)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Add any additional notes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            // Bill Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bill Summary",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", fontSize = 14.sp)
                            Text("LKR $subtotal", fontSize = 14.sp)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Platform Fee:", fontSize = 14.sp)
                            Text("LKR $platformFee", fontSize = 14.sp)
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "LKR $total",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        
        // Confirm Bill button
        Button(
            onClick = onConfirmBill,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = S_YELLOW),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "✓ Confirm Bill",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ServiceItemCard(
    serviceItem: ServiceItem,
    onValueChange: (ServiceItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = serviceItem.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "LKR 0.00",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            
            Text(
                text = serviceItem.rate,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = serviceItem.unit,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            
            OutlinedTextField(
                value = if (serviceItem.unit.contains("Hours")) serviceItem.hoursWorked else serviceItem.squareFeet,
                onValueChange = { value ->
                    if (serviceItem.unit.contains("Hours")) {
                        onValueChange(serviceItem.copy(hoursWorked = value))
                    } else {
                        onValueChange(serviceItem.copy(squareFeet = value))
                    }
                },
                placeholder = { 
                    Text(
                        if (serviceItem.unit.contains("Hours")) "Enter hours worked" else "Enter square feet"
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun AdditionalCostItem(
    cost: AdditionalCost,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cost.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "LKR ${cost.amount}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.Red
            )
        }
    }
}

@Preview
@Composable
fun CreateServiceBillScreenPreview() {
    CreateServiceBillScreen(
        booking = Booking(
            id = "1",
            customerName = "John Smith",
            serviceName = "Kitchen Plumbing Repair",
            description = "Kitchen sink repair",
            scheduledDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // Tomorrow
            scheduledTime = "10:00 AM",
            status = BookingStatus.ACCEPTED,
            createdAt = System.currentTimeMillis() - (2 * 60 * 60 * 1000) // 2 hours ago
        ),
        onBackClick = {},
        onConfirmBill = {}
    )
}
