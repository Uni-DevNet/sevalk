package com.sevalk.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.sevalk.R
import com.sevalk.presentation.auth.AuthState
import com.sevalk.presentation.auth.AuthViewModel

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val authState by remember { derivedStateOf { authViewModel.authState } }

    LaunchedEffect(Unit) {
        // Show splash screen for at least 2 seconds
        delay(2000)
        authViewModel.checkInitialAuthState()
    }

    // Handle navigation based on auth state
    LaunchedEffect(authState) {
        when (authState) {
            AuthState.FIRST_TIME_USER -> {
                delay(500) // Small delay for smooth transition
                onNavigateToOnboarding()
            }
            AuthState.AUTHENTICATED -> {
                delay(500)
                onNavigateToHome()
            }
            AuthState.UNAUTHENTICATED -> {
                delay(500)
                onNavigateToLogin()
            }
            AuthState.LOADING -> {
                // Stay on splash screen
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_430_430),
                contentDescription = "SevaLK Logo",
                modifier = Modifier.size(420.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SevaLK",
                fontSize = 62.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Your trusted service partner",
                fontSize = 18.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}
