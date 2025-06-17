package com.sevalk.presentation.auth.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sevalk.R
import com.sevalk.data.models.UserType
import com.sevalk.presentation.auth.components.AuthHeader
import com.sevalk.presentation.auth.components.CustomTextField
import com.sevalk.presentation.components.common.PrimaryButton
import com.sevalk.presentation.components.common.ProgressIndicator
import com.sevalk.ui.theme.S_INPUT_BACKGROUND
import com.sevalk.ui.theme.S_LIGHT_BLACK
import com.sevalk.ui.theme.S_LIGHT_TEXT
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.SevaLKTheme

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column {
            AuthHeader(
                title = when (uiState.currentStep) {
                    1 -> "Let's Get Started"
                    2 -> "Verify Your Email"
                    3 -> "Almost There"
                    else -> "Registration"
                },
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
            ) {
                ProgressIndicator(
                    currentStep = uiState.currentStep,
                    totalSteps = 3
                )
                Spacer(modifier = Modifier.height(40.dp))
                Step3AlmostThere(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
fun Step1GetStarted(
    uiState: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Column {
        // Full Name TextField
        CustomTextField(
            value = uiState.fullName,
            onValueChange = { onEvent(RegistrationEvent.FullNameChanged(it)) },
            label = "Full Name",
            placeholder = "Enter your full name",
            leadingIcon = painterResource(id = R.drawable.user),
            keyboardType = KeyboardType.Text,
        )

        // Email TextField
        CustomTextField(
            value = uiState.email,
            onValueChange = { onEvent(RegistrationEvent.EmailChanged(it)) },
            label = "Email Address",
            placeholder = "Enter your email",
            leadingIcon = painterResource(id = R.drawable.email),
            keyboardType = KeyboardType.Email,
        )

        Spacer(modifier = Modifier.height(36.dp))

        PrimaryButton(
            text = "Next: Verify Email",
            onClick = {
                // TODO: Add validation logic here
            }
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
                text = "Or sign up using",
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
                text = "Already have an account? ",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Login",
                color = Color(0xFFFFC107),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {  }
            )
        }
    }
}


@Composable
fun Step2VerifyEmail(
    uiState: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Column {
        Text(
            text = "We've sent a verification email to ${uiState.email}. Please check your inbox (and spam folder) and click the verification link or enter the code below.",
            textAlign = TextAlign.Justify,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Verification Code",
            color = S_LIGHT_TEXT,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..5) {
                TextField(
                    value = uiState.verificationCode[i],
                    onValueChange = { onEvent(RegistrationEvent.VerificationCodeChanged(i, it)) },
                    modifier = Modifier
                        .weight(1f),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), // 12dp rounded corners
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = S_INPUT_BACKGROUND,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Resend Verification Code",
            color = S_YELLOW,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { /* TODO: Resend code */ }
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(64.dp))
        PrimaryButton(
            text = "Verify & Continue",
            onClick = {}
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Entered the wrong email? ",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Go Back",
                color = Color(0xFFFFC107),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* TODO: Add logic to go back */ }
            )
        }
    }
}

@Composable
fun Step3AlmostThere(
    uiState: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Column {
        Text(
            "How will you primarily use SevaLK?",
            fontWeight = FontWeight.Medium,
            color = S_LIGHT_TEXT,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.padding(start = 8.dp),
        ) {
            UserType.entries.forEach { userType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(RegistrationEvent.UserTypeChanged(userType)) }
                ) {
                    RadioButton(
                        selected = uiState.userType == userType,
                        onClick = { onEvent(RegistrationEvent.UserTypeChanged(userType)) },
                        colors = RadioButtonDefaults.colors(selectedColor = S_YELLOW),
                    )
                    Text(text = userType.displayName, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
        HorizontalDivider(
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(36.dp))

        CustomTextField(
            value = uiState.password,
            onValueChange = { onEvent(RegistrationEvent.PasswordChanged(it)) },
            label = "Create Password",
            placeholder = "Enter your password",
            leadingIcon = painterResource(id = R.drawable.lock),
            keyboardType = KeyboardType.Password,
            trailingIcon = if (uiState.isPasswordVisible) {
                painterResource(id = R.drawable.eye_slash)
            } else {
                painterResource(id = R.drawable.eye)
            },
        )

        CustomTextField(
            value = uiState.confirmPassword,
            onValueChange = { onEvent(RegistrationEvent.ConfirmPasswordChanged(it)) },
            label = "Confirm Password",
            placeholder = "Re Enter your password",
            leadingIcon = painterResource(id = R.drawable.lock),
            keyboardType = KeyboardType.Password,
            trailingIcon = if (uiState.isPasswordVisible) {
                painterResource(id = R.drawable.eye_slash)
            } else {
                painterResource(id = R.drawable.eye)
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Create My Account",
            onClick = {
                // TODO: Create account logic
            }
        )
    }
}


@Preview
@Composable
fun RegistrationScreenPreview() {
    SevaLKTheme {
        RegistrationScreen()
    }
}