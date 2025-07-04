package com.sevalk.presentation.provider.jobs.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sevalk.data.models.Job

@Composable
fun AcceptedJobsSection(
    todaysEarnings: String,
    jobsToday: Int,
    jobs: List<Job>,
    onViewDetails: (String) -> Unit,
    onCreateBill: (String) -> Unit
) {
    Column {
        // Status cards row
        StatusCardsRow(
            todaysEarnings = todaysEarnings,
            jobsToday = jobsToday
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // Accepted jobs list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(jobs) { job ->
                AcceptedJobCard(
                    job = job,
                    onViewDetails = { onViewDetails(job.id) },
                    onCreateBill = { onCreateBill(job.id) }
                )
            }
        }
    }
}
