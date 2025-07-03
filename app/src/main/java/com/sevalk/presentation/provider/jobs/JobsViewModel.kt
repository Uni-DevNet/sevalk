package com.sevalk.presentation.provider.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.Job
import com.sevalk.data.models.JobStatus
import com.sevalk.data.models.JobsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobsViewModel : ViewModel() {

    private val _state = MutableStateFlow(JobsState())
    val state: StateFlow<JobsState> = _state.asStateFlow()

    init {
        loadJobs()
    }

    fun onFilterChanged(status: JobStatus) {
        _state.value = _state.value.copy(currentFilter = status)
        loadJobsForFilter(status)
    }

    fun onAcceptJob(jobId: String) {
        viewModelScope.launch {
            // Implement job acceptance logic
            val updatedJobs = _state.value.jobs.map { job ->
                if (job.id == jobId) job.copy(status = JobStatus.ACCEPTED) else job
            }
            _state.value = _state.value.copy(jobs = updatedJobs)
        }
    }

    private fun loadJobs() {
        // Mock data - replace with actual repository call
        val mockJobs = listOf(
            Job(
                id = "1",
                clientName = "Sarah Johnson",
                clientRating = 4.8f,
                title = "Kitchen Plumbing Repair",
                description = "Kitchen sink is leaking from the pipes underneath. Water...",
                date = "2025-06-07",
                time = "10:00 AM",
                distance = "1.2 km",
                timeAgo = "2 hours ago",
                status = JobStatus.ACCEPTED
            ),
            Job(
                id = "2",
                clientName = "Mike Chen",
                clientRating = 4.6f,
                title = "Bathroom Pipe Installation",
                description = "New bathroom renovation - need pipes connected for...",
                date = "2025-06-08",
                time = "11:00 AM",
                distance = "2.2 km",
                timeAgo = "2 hours ago",
                status = JobStatus.ACCEPTED
            )
        )

        _state.value = _state.value.copy(
            todaysEarnings = "LKR 2500",
            jobsToday = 4,
            jobs = mockJobs
        )
    }

    private fun loadJobsForFilter(status: JobStatus) {
        // Filter jobs based on status
        loadJobs() // Reload and filter
    }
}