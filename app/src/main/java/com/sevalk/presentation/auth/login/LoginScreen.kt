package com.sevalk.presentation.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevalk.ui.theme.SevaLKTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.sevalk.R
import com.sevalk.presentation.auth.components.AuthHeader
import com.sevalk.presentation.auth.components.CustomTextField
import com.sevalk.presentation.components.common.PrimaryButton
import com.sevalk.ui.theme.S_INPUT_BACKGROUND


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column{
            AuthHeader(title = "Welcome Back", showBackButton = false)
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Email TextField
                CustomTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email Address",
                    placeholder = "Enter your email",
                    leadingIcon = painterResource(id = R.drawable.email),
                    keyboardType = KeyboardType.Email,
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError
                )

                // Password TextField
                CustomTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = painterResource(id = R.drawable.lock),
                    keyboardType = KeyboardType.Password,
                    isPasswordField = true,
                    isPasswordVisible = uiState.isPasswordVisible,
                    trailingIcon = if (uiState.isPasswordVisible) {
                        painterResource(id = R.drawable.eye_slash)
                    } else {
                        painterResource(id = R.drawable.eye)
                    },
                    onTrailingIconClick = viewModel::onTogglePasswordVisibility,
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError
                )
                
                Text(
                    text = "Forgot Password?",
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { viewModel.forgotPassword() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Show general error message if any
                uiState.errorMessage?.let { errorMessage ->
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Login Button
                PrimaryButton(
                    text = if (uiState.isLoading) "Logging in..." else "Login",
                    onClick = {
                        viewModel.onLoginClick(onSuccess = onLoginSuccess)
                    },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Or continue with divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                    Text(
                        text = "Or continue with",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { /* TODO: add logic */ },
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = S_INPUT_BACKGROUND
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Sign-In",
                        modifier = Modifier.size(34.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))


                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Sign Up",
                        color = Color(0xFFFFC107),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToSignUp() }
                    )
                }
            }

        }
    }
}




@Preview
@Composable
fun LoginScreenPreview() {
    SevaLKTheme {
        LoginScreen()
    }
}
