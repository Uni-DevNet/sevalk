package com.sevalk.presentation.customer.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.R
import com.sevalk.ui.theme.S_YELLOW
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.sevalk.presentation.components.common.PrimaryButton
import com.sevalk.presentation.components.common.PrimaryButtonStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedService by remember { mutableStateOf("Cleaning") }
    var bookingTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedTab == 1) {
        selectedTab = 0
    }

    BackHandler(enabled = selectedTab == 0) {
        onNavigateBack()
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { dateMillis ->
                dateMillis?.let {
                    val date = Date(it)
                    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    selectedDate = formatter.format(date)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                selectedTime = time
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Booking Confirmation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(50.dp),
                                   
                                )

                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color.Green, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "John Doe",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                    Text(
                                        text = " 4.8 (127)",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = " • 0.8 km",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Plumbing",
                                        color = Color(0xFF2196F3),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF2196F3).copy(alpha = 0.1f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = "Cleaning",
                                        color = Color(0xFF2196F3),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF2196F3).copy(alpha = 0.1f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            IconButton(onClick = { isFavorite = !isFavorite }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Jobs",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " 340 jobs",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            1 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box {
                               
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(50.dp),
                                   
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color.Green, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "John Doe",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                    Text(
                                        text = " 4.8 (127)",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = " • 0.8 km",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Plumbing",
                                        color = Color(0xFF2196F3),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF2196F3).copy(alpha = 0.1f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    Text(
                                        text = "Cleaning",
                                        color = Color(0xFF2196F3),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF2196F3).copy(alpha = 0.1f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            IconButton(onClick = { isFavorite = !isFavorite }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Jobs",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " 340 jobs",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
  
        Text(
            text = if (selectedTab == 0) "Select Service/s" else "Schedule & Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (selectedTab >= 0) Color(0xFFFFC107) else Color.Gray,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedTab > 0) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "1",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(if (selectedTab > 0) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (selectedTab >= 1) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            0 -> {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Available Services",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    ServiceCard(
                        title = "Plumbing",
                        subtitle = "Fixing and installing water systems",
                        duration = "90 minutes",
                        price = "LKR 400/hr",
                        isSelected = selectedService == "Plumbing",
                        onSelect = { selectedService = "Plumbing" }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    ServiceCard(
                        title = "Cleaning",
                        subtitle = "General home cleaning services",
                        duration = "Full Day",
                        price = "LKR 4000/day",
                        isSelected = selectedService == "Cleaning",
                        onSelect = { selectedService = "Cleaning" }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = "Continue to Details",
                        onClick = { selectedTab = 1 },
                        backgroundColor = S_YELLOW,
                        foregroundColor = Color.White
                    )
                }
            }
            1 -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = "Booking Title",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = bookingTitle,
                        onValueChange = { bookingTitle = it },
                        placeholder = { 
                            Text(
                                "Eg: Kitchen Plumbing Repair",
                                color = Color.Gray
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFFFFC107)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { 
                            Text(
                                "Provide simple description what to do...",
                                color = Color.Gray
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFFFFC107)
                        ),
                        maxLines = 5,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Schedule Date/ Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { },
                            placeholder = { 
                                Text(
                                    "mm/dd/yyyy",
                                    color = Color.Gray
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedBorderColor = Color(0xFFFFC107)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.Black
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Date",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            readOnly = true,
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                                showDatePicker = true
                                            }
                                        }
                                    }
                                }
                        )
                        
                        OutlinedTextField(
                            value = selectedTime,
                            onValueChange = { },
                            placeholder = { 
                                Text(
                                    "Select Time",
                                    color = Color.Gray
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedBorderColor = Color(0xFFFFC107)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.Black
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showTimePicker = true }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.clock_24_24),
                                        contentDescription = "Time",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            readOnly = true,
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                                showTimePicker = true
                                            }
                                        }
                                    }
                                }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(118.dp))

                    PrimaryButton(
                        text = "Confirm Booking",
                        onClick = { },
                        backgroundColor = S_YELLOW,
                        foregroundColor = Color.White,
                        style = PrimaryButtonStyle.ICON_TEXT
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { 
                onTimeSelected(timePickerState.hour, timePickerState.minute) 
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
private fun ServiceCard(
    title: String,
    subtitle: String,
    duration: String,
    price: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF8E1) else Color.White
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFC107)) 
        else 
            androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("⏰", fontSize = 12.sp)
                    Text(
                        text = " $duration",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            
            Text(
                text = price,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}