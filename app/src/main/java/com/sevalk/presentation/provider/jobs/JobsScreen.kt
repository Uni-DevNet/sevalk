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
import com.sevalk.data.models.Job
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
    viewModel: JobsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }
    
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
                        jobs = state.jobs,
                        onViewDetails = { jobId ->
                            selectedJob = state.jobs.find { it.id == jobId }
                            showBottomSheet = true
                        },
                        onCreateBill = { jobId -> /* Handle create bill */ }
                    )
                }
                JobStatus.DONE -> {
                    DoneJobsSection(
                        jobs = state.jobs,
                        onViewDetails = { jobId ->
                            selectedJob = state.jobs.find { it.id == jobId }
                            showBottomSheet = true
                        }
                    )
                }
                else -> {
                    DefaultJobsSection(
                        jobs = state.jobs,
                        onViewDetails = { jobId ->
                            selectedJob = state.jobs.find { it.id == jobId }
                            showBottomSheet = true
                        },
                        onQuickAccept = { jobId -> viewModel.onAcceptJob(jobId) }
                    )
                }
            }
        }
        
        // Show bottom sheet when job is selected
        selectedJob?.let { job ->
            if (showBottomSheet) {
                JobDetailsBottomSheet(
                    job = job,
                    onDismiss = { 
                        showBottomSheet = false
                        selectedJob = null
                    },
                    onCall = { /* Handle call */ },
                    onMessage = { /* Handle message */ },
                    onDecline = { 
                        /* Handle decline */
                        showBottomSheet = false
                        selectedJob = null
                    },
                    onAccept = { 
                        viewModel.onAcceptJob(job.id)
                        showBottomSheet = false
                        selectedJob = null
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
