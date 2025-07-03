package com.sevalk.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text("Provider Onboarding Screen")
        // TODO: Add onboarding content and call onComplete() when done
    }
}
