package com.sevalk.presentation.provider.jobs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sevalk.data.models.Job

@Composable
fun DefaultJobsSection(
    jobs: List<Job>,
    onViewDetails: (String) -> Unit,
    onQuickAccept: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(jobs) { job ->
            JobCard(
                job = job,
                onViewDetails = { onViewDetails(job.id) },
                onQuickAccept = { onQuickAccept(job.id) }
            )
        }
    }
}
