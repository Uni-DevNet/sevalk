package com.sevalk.presentation.customer.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sevalk.R
import com.sevalk.presentation.components.map.MapService
import com.sevalk.presentation.components.map.ServiceProvider
import com.sevalk.presentation.components.map.ServiceType
import com.sevalk.presentation.components.map.SearchBar
import com.sevalk.presentation.components.map.ServiceTypeFilters
import com.sevalk.data.repository.ServiceProviderRepository
import com.sevalk.presentation.components.common.PrimaryButton
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.res.painterResource
import com.sevalk.ui.theme.S_YELLOW
import androidx.navigation.NavController
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@Composable
fun ServiceProviderMapScreen(
    navController: NavController? = null,
    onNavigateToBooking: () -> Unit = {}
) {
    var selectedServiceType by remember { mutableStateOf(ServiceType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf<ServiceProvider?>(null) }
    val allServiceProviders = remember { ServiceProviderRepository.getServiceProviders() }
    val filteredProviders = remember(searchQuery, selectedServiceType) {
        val searchFiltered = if (searchQuery.isBlank()) {
            allServiceProviders
        } else {
            ServiceProviderRepository.searchServiceProviders(searchQuery)
        }
        
        if (selectedServiceType == ServiceType.ALL) {
            searchFiltered
        } else {
            searchFiltered.filter { it.type == selectedServiceType }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapService(
            serviceProviders = filteredProviders,
            selectedServiceType = selectedServiceType,
            showCurrentLocation = true,
            onMarkerClick = { provider ->
                selectedProvider = provider
            },
            modifier = Modifier.fillMaxSize()
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopStart)
                .offset(y = 10.dp)
                .zIndex(1f)
        )

        ServiceTypeFilters(
            selectedType = selectedServiceType,
            onTypeSelected = { selectedServiceType = it },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .offset(y = 80.dp)
                .zIndex(1f)
        )
        selectedProvider?.let { provider ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .zIndex(2f)
                    .clickable { selectedProvider = null }
            )
            
            AnimatedVisibility(
                visible = selectedProvider != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(3f)
            ) {
                ProviderInfoCard(
                    provider = provider,
                    onDismiss = { selectedProvider = null },
                    onBookNow = {
                        selectedProvider = null
                        navController?.navigate("booking") ?: onNavigateToBooking()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 1.dp)
                )
            }
        }
    }
}

@Composable
fun ProviderInfoCard(
    provider: ServiceProvider,
    onDismiss: () -> Unit,
    onBookNow: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
                    .clickable { onDismiss() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Provider Details",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.2f),
                            androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${provider.rating} (127)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "• 0.8 km",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Text(
                        text = provider.type.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2196F3)
                    )
                }
                
                IconButton(onClick = { isFavorite = !isFavorite }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Jobs",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "340 jobs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.circle_dollar_sign_14_14),
                        contentDescription = "Price",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LKR 800/hr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "Book Now",
                    onClick = onBookNow,
                    modifier = Modifier.weight(1f),
                    backgroundColor = S_YELLOW,
                    foregroundColor = Color.White
                )
                
                Button(
                    onClick = {},
                    modifier = Modifier.size(52.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.phone_20_20),
                        contentDescription = "Call",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Button(
                    onClick = {},
                    modifier = Modifier.size(52.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.message_circle_20_20),
                        contentDescription = "Message",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
