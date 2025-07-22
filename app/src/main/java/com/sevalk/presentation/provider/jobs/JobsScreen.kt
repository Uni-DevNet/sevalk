package com.sevalk.presentation.provider.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sevalk.data.models.Booking
import com.sevalk.presentation.provider.jobs.components.JobsHeader
import com.sevalk.presentation.provider.jobs.components.AcceptedJobsSection
import com.sevalk.presentation.provider.jobs.components.DoneJobsSection
import com.sevalk.presentation.provider.jobs.components.DefaultJobsSection
import com.sevalk.data.models.JobStatus
import com.sevalk.presentation.provider.jobs.components.JobDetailsBottomSheet
import com.sevalk.ui.theme.SevaLKTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: JobsViewModel = viewModel(),
    onNavigateToCreateBill: (Booking) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header with filter tabs
            JobsHeader(
                currentFilter = state.currentFilter,
                onFilterChanged = { viewModel.onFilterChanged(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Conditional content based on filter
            when (state.currentFilter) {
                JobStatus.ACCEPTED -> {
                    AcceptedJobsSection(
                        todaysEarnings = state.todaysEarnings,
                        jobsToday = state.jobsToday,
                        bookings = state.bookings,
                        onViewDetails = { bookingId ->
                            selectedBooking = state.bookings.find { it.id == bookingId }
                            showBottomSheet = true
                        },
                        onCreateBill = { bookingId -> 
                            state.bookings.find { it.id == bookingId }?.let { booking ->
                                onNavigateToCreateBill(booking)
                            }
                        }
                    )
                }
                JobStatus.DONE -> {
                    DoneJobsSection(
                        bookings = state.bookings,
                        onViewDetails = { bookingId ->
                            selectedBooking = state.bookings.find { it.id == bookingId }
                            showBottomSheet = true
                        }
                    )
                }
                else -> {
                    DefaultJobsSection(
                        bookings = state.bookings,
                        onViewDetails = { bookingId ->
                            selectedBooking = state.bookings.find { it.id == bookingId }
                            showBottomSheet = true
                        },
                        onQuickAccept = { bookingId -> viewModel.onAcceptBooking(bookingId) }
                    )
                }
            }
        }
        
        // Show bottom sheet when booking is selected
        selectedBooking?.let { booking ->
            if (showBottomSheet) {
                JobDetailsBottomSheet(
                    booking = booking,
                    onDismiss = { 
                        showBottomSheet = false
                        selectedBooking = null
                    },
                    onCall = { /* Handle call */ },
                    onMessage = { /* Handle message */ },
                    onDecline = { 
                        /* Handle decline */
                        showBottomSheet = false
                        selectedBooking = null
                    },
                    onAccept = { 
                        viewModel.onAcceptBooking(booking.id)
                        showBottomSheet = false
                        selectedBooking = null
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun JobsScreenPreview() {
    SevaLKTheme {
        JobsScreen()
    }
}

