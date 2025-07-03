package com.sevalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sevalk.presentation.navigation.AppNavigation
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sevalk.presentation.navigation.SevaLKNavigation

import com.sevalk.ui.theme.SevaLKTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SevaLKTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.fillMaxSize())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    SevaLKNavigation()

                }
            }
        }
    }
}