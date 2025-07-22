package com.sevalk.presentation.provider.jobs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sevalk.data.models.Booking

@Composable
fun DoneJobsSection(
    bookings: List<Booking>,
    onViewDetails: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(bookings) { booking ->
            CompletedJobCard(
                booking = booking,
                onViewDetails = { onViewDetails(booking.id) }
            )
        }
    }
}
