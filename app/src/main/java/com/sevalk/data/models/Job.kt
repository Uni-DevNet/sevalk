package com.sevalk.data.models

data class Job(
    val id: String,
    val clientName: String,
    val clientRating: Float,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val distance: String,
    val timeAgo: String,
    val status: JobStatus = JobStatus.NEW
)

enum class JobStatus {
    NEW, ACCEPTED, DONE, UNPAID
}

data class JobsState(
    val todaysEarnings: String = "LKR 0",
    val jobsToday: Int = 0,
    val currentFilter: JobStatus = JobStatus.NEW,
    val jobs: List<Job> = emptyList(),
    val isLoading: Boolean = false
)
