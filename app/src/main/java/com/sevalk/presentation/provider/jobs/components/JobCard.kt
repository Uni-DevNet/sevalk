package com.sevalk.presentation.provider.jobs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.data.models.Booking
import com.sevalk.data.models.BookingStatus
import com.sevalk.data.models.JobStatus
import com.sevalk.data.models.toJobDate
import com.sevalk.data.models.toJobDescription
import com.sevalk.data.models.toJobDistance
import com.sevalk.data.models.toJobTime
import com.sevalk.data.models.toJobTimeAgo
import com.sevalk.data.models.toJobTitle
import com.sevalk.data.models.toJobDate
import com.sevalk.data.models.toJobDescription
import com.sevalk.data.models.toJobDistance
import com.sevalk.data.models.toJobTime
import com.sevalk.data.models.toJobTimeAgo
import com.sevalk.data.models.toJobTitle
import com.sevalk.ui.theme.S_LIGHT_BLACK
import com.sevalk.ui.theme.S_STROKE_COLOR
import com.sevalk.ui.theme.S_YELLOW

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobCard(
    booking: Booking,
    onViewDetails: () -> Unit,
    onQuickAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with client info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = booking.customerName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = S_YELLOW,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "4.8", // Static rating for now, can be added to Booking model later
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Text(
                    text = booking.toJobTimeAgo(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Job title and description
            Text(
                text = booking.toJobTitle(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = booking.toJobDescription(),
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Job details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                JobDetailItem(
                    icon = Icons.Default.DateRange,
                    text = booking.toJobDate()
                )
                JobDetailItem(
                    icon = Icons.Default.Schedule,
                    text = booking.toJobTime()
                )
                JobDetailItem(
                    icon = Icons.Default.LocationOn,
                    text = booking.toJobDistance()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye, // Use appropriate icon
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Details")
                }
                
                Button(
                    onClick = onQuickAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = S_YELLOW
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Quick Accept",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun JobDetailItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}


@Preview
@Composable
fun JobCardPreview() {
    JobCard(
        booking = Booking(
            id = "1",
            customerName = "Sarah Johnson",
            serviceName = "Kitchen Plumbing Repair",
            description = "Kitchen sink is leaking from the pipes underneath. Water...",
            scheduledDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // Tomorrow
            scheduledTime = "10:00 AM",
            status = com.sevalk.data.models.BookingStatus.ACCEPTED,
            createdAt = System.currentTimeMillis() - (2 * 60 * 60 * 1000) // 2 hours ago
        ),
        onViewDetails = {},
        onQuickAccept = {}
    )
}