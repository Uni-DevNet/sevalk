package com.sevalk.presentation.provider.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.Booking
import com.sevalk.data.models.BookingStatus
import com.sevalk.data.models.JobStatus
import com.sevalk.data.models.JobsState
import com.sevalk.data.models.toBookingStatus
import com.sevalk.data.models.toJobStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {

    private val _state = MutableStateFlow(JobsState())
    val state: StateFlow<JobsState> = _state.asStateFlow()

    init {
        loadBookings()
    }

    fun onFilterChanged(status: JobStatus) {
        _state.value = _state.value.copy(currentFilter = status)
        loadBookingsForFilter(status)
    }

    fun onAcceptBooking(bookingId: String) {
        viewModelScope.launch {
            // Implement booking acceptance logic
            val updatedBookings = _state.value.bookings.map { booking ->
                if (booking.id == bookingId) booking.copy(status = BookingStatus.ACCEPTED) else booking
            }
            _state.value = _state.value.copy(bookings = updatedBookings)
        }
    }

    private fun loadBookings() {
        // Mock data - replace with actual repository call
        val mockBookings = listOf(
            Booking(
                id = "1",
                customerName = "Sarah Johnson",
                serviceName = "Kitchen Plumbing Repair",
                description = "Kitchen sink is leaking from the pipes underneath. Water...",
                scheduledDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // Tomorrow
                scheduledTime = "10:00 AM",
                status = BookingStatus.ACCEPTED,
                createdAt = System.currentTimeMillis() - (2 * 60 * 60 * 1000) // 2 hours ago
            ),
            Booking(
                id = "2",
                customerName = "Mike Chen",
                serviceName = "Bathroom Pipe Installation",
                description = "New bathroom renovation - need pipes connected for...",
                scheduledDate = System.currentTimeMillis() + (48 * 60 * 60 * 1000), // Day after tomorrow
                scheduledTime = "11:00 AM",
                status = BookingStatus.ACCEPTED,
                createdAt = System.currentTimeMillis() - (2 * 60 * 60 * 1000) // 2 hours ago
            )
        )

        _state.value = _state.value.copy(
            todaysEarnings = "LKR 2500",
            jobsToday = 4,
            bookings = mockBookings
        )
    }

    private fun loadBookingsForFilter(status: JobStatus) {
        // Filter bookings based on status
        loadBookings() // Reload and filter
    }
}