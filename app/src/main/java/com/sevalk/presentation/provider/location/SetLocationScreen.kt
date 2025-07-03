package com.sevalk.presentation.provider.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sevalk.R
import com.sevalk.data.models.LocationMethod
import com.sevalk.data.models.ServiceLocation
import com.sevalk.presentation.components.common.PrimaryButton
import com.sevalk.ui.theme.SevaLKTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLocationScreen(
    viewModel: SetLocationViewModel = viewModel(),
    onSetupComplete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val serviceRadius by viewModel.serviceRadius.collectAsState()
    
    LaunchedEffect(uiState.setupCompleted) {
        if (uiState.setupCompleted) {
            onSetupComplete()
        }
    }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Set Your Service Location",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "Let customers know where you provide your services",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // Location Method Selection
            LocationMethodSection(
                methods = uiState.locationMethods,
                onMethodSelected = viewModel::selectLocationMethod
            )

            // Primary Service Location
            uiState.primaryLocation?.let { location ->
                PrimaryLocationSection(
                    location = location,
                    onChangeLocation = viewModel::changeLocation
                )
            }

            // Service Radius
            ServiceRadiusSection(
                radius = serviceRadius,
                onRadiusChange = viewModel::updateServiceRadius
            )

            Spacer(modifier = Modifier.weight(1f))

            // Complete Setup Button
            PrimaryButton(
                text = "Complete Setup",
                onClick = viewModel::completeSetup
            )
        }
    }
}

@Composable
private fun LocationMethodSection(
    methods: List<LocationMethod>,
    onMethodSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Choose Location method",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        
        methods.forEach { method ->
            LocationMethodCard(
                method = method,
                onSelected = { onMethodSelected(method.id) }
            )
        }
    }
}

@Composable
private fun LocationMethodCard(
    method: LocationMethod,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (method.isSelected) Color(0xFFFFF8E1) else Color.White
        ),
        border = if (method.isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFC107))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFFC107)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (method.id == "current_location") painterResource(id = R.drawable.crosshair) else painterResource(id = R.drawable.globe),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = method.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = method.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Check mark
            if (method.isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryLocationSection(
    location: ServiceLocation,
    onChangeLocation: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Primary Service Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            TextButton(
                onClick = onChangeLocation,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFFC107)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Change")
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = location.address,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${location.city}, ${location.province}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ServiceRadiusSection(
    radius: Float,
    onRadiusChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Service Radius: ${radius.toInt()} km",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        
        Slider(
            value = radius,
            onValueChange = onRadiusChange,
            valueRange = 1f..50f,
            steps = 49,
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color(0xFFFFC107),
                inactiveTrackColor = Color.LightGray
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 km", fontSize = 12.sp, color = Color.Gray)
            Text("25 km", fontSize = 12.sp, color = Color.Gray)
            Text("50 km", fontSize = 12.sp, color = Color.Gray)
        }
        
        Text(
            text = "You'll be visible to customers within ${radius.toInt()} km of your location",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun PreviewSetLocationScreen() {
    SevaLKTheme {
        SetLocationScreen()
    }
}