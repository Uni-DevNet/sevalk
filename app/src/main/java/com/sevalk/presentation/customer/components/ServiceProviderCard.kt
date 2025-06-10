package com.sevalk.presentation.customer.components

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ServiceProviderCard(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Text("Service Provider Card")
    }
}
