package com.sevalk.presentation.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.sevalk.ui.theme.S_YELLOW

@Composable
fun LogoutConfirmationDialog(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = S_YELLOW)
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun LogoutButton(
    onLogoutSuccess: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showLogoutDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text("Logout", color = Color.White)
    }

    LogoutConfirmationDialog(
        isVisible = showLogoutDialog,
        onConfirm = {
            authViewModel.signOut()
            showLogoutDialog = false
            onLogoutSuccess()
        },
        onDismiss = {
            showLogoutDialog = false
        }
    )
}
