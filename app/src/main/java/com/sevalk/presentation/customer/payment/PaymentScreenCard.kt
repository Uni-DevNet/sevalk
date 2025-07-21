package com.sevalk.presentation.customer.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Import this for drawable resources
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sevalk.R
import com.sevalk.data.models.PaymentMethodType
import com.sevalk.data.repositories.PaymentRepository
import com.sevalk.domain.payment.PaymentViewModel
import com.sevalk.ui.theme.S_BLUE
import com.sevalk.ui.theme.S_BLUE_BACKGROUND
import com.sevalk.ui.theme.S_GREEN
import com.sevalk.ui.theme.S_INPUT_BACKGROUND
import com.sevalk.ui.theme.S_LIGHT_TEXT
import com.sevalk.ui.theme.S_PLACEHOLDER
import com.sevalk.ui.theme.S_STROKE_COLOR
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.SevaLKTheme
import com.sevalk.data.models.CardDetails as ModelCardDetails

private const val DUMMY_BOOKING_ID = "test_booking_123"
private const val DUMMY_CUSTOMER_ID = "test_customer_123"
private const val DUMMY_PROVIDER_ID = "test_provider_123"
private const val DUMMY_AMOUNT = 2500.0

private fun isValidCardNumber(number: String): Boolean =
    number.replace(" ", "").length == 16

private fun formatExpiryDate(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return when {
        digitsOnly.length <= 2 -> digitsOnly
        else -> "${digitsOnly.take(2)}/${digitsOnly.drop(2).take(2)}"
    }
}

private fun isValidExpiryDate(date: String): Boolean {
    if (!date.matches(Regex("^\\d{2}/\\d{2}$"))) return false

    val (month, year) = date.split("/").map { it.toInt() }
    val currentYear = java.time.Year.now().value % 100

    return month in 1..12 && year >= currentYear
}

private fun isValidCvv(cvv: String): Boolean = cvv.length in 3..4

private fun isValidCardholderName(name: String): Boolean {
    val trimmedName = name.trim()
    return trimmedName.length >= 2 && // At least 2 characters
           trimmedName.all { it.isLetterOrDigit() || it.isWhitespace() || it == '-' || it == '\'' } && // Allow letters, spaces, hyphens, apostrophes
           !trimmedName.contains(Regex("\\s{2,}")) && // No consecutive spaces
           trimmedName.first().isLetter() // Must start with a letter
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod>(PaymentMethod.CreditDebitCard) }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }

    // Add validation states
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var expiryDateError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }
    var cardholderNameError by remember { mutableStateOf<String?>(null) }

    // Handle payment success
    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            onPaymentSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )

        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background) // Use theme background
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Button(
                    onClick = {
                        // Reset errors
                        cardNumberError = null
                        expiryDateError = null
                        cvvError = null
                        cardholderNameError = null

                        var isValid = true

                        if (selectedPaymentMethod == PaymentMethod.CreditDebitCard) {
                            // Validate card number
                            if (!isValidCardNumber(cardNumber)) {
                                cardNumberError = "Enter a valid 16-digit card number"
                                isValid = false
                            }

                            // Validate expiry date
                            if (!isValidExpiryDate(expiryDate)) {
                                expiryDateError = "Enter a valid expiry date (MM/YY)"
                                isValid = false
                            }

                            // Validate CVV
                            if (!isValidCvv(cvv)) {
                                cvvError = "Enter a valid CVV"
                                isValid = false
                            }

                            // Validate cardholder name
                            if (!isValidCardholderName(cardholderName)) {
                                cardholderNameError = "Please enter a valid name"
                                isValid = false
                            }
                        }

                        if (isValid) {
                            val paymentMethodType = when (selectedPaymentMethod) {
                                PaymentMethod.Cash -> PaymentMethodType.CASH
                                PaymentMethod.CreditDebitCard -> PaymentMethodType.CREDIT_CARD
                            }

                            val cardDetails = if (selectedPaymentMethod == PaymentMethod.CreditDebitCard) {
                                ModelCardDetails(
                                    last4Digits = cardNumber.takeLast(4),
                                    cardType = "VISA",
                                    expiryMonth = expiryDate.split("/").firstOrNull()?.toIntOrNull() ?: 0,
                                    expiryYear = expiryDate.split("/").lastOrNull()?.toIntOrNull() ?: 0
                                )
                            } else null

                            viewModel.processPayment(
                                amount = DUMMY_AMOUNT,
                                paymentMethod = paymentMethodType,
                                cardDetails = cardDetails,
                                bookingId = DUMMY_BOOKING_ID,
                                customerId = DUMMY_CUSTOMER_ID,
                                providerId = DUMMY_PROVIDER_ID
                            )
                        }
                    },
                    enabled = when (selectedPaymentMethod) {
                        PaymentMethod.CreditDebitCard -> {
                            cardNumber.isNotBlank() && expiryDate.isNotBlank() &&
                            cvv.isNotBlank() && cardholderName.isNotBlank()
                        }
                        PaymentMethod.Cash -> true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = S_YELLOW),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    // Add a Row to align the icon and text horizontally
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center, // Center the content within the button
                        modifier = Modifier.fillMaxWidth() // Ensure the row fills the button width for centering
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.dollar_sign), // Your dollar sign icon
                            contentDescription = "Dollar Sign", // Content description for accessibility
                            tint = MaterialTheme.colorScheme.onPrimary, // Match text color, typically black for S_YELLOW
                            modifier = Modifier.size(24.dp) // Adjust size as needed
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                        Text(
                            text = "Pay LKR 2500.00",
                            color = MaterialTheme.colorScheme.onPrimary, // Text on S_YELLOW should be Black as per your theme
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "By proceeding, you agree to our Terms of Service and\nacknowledge our Privacy Policy",
                    style = MaterialTheme.typography.labelSmall.copy(color = S_LIGHT_TEXT),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(S_INPUT_BACKGROUND)
                    .padding(16.dp)
            ) {
                // Total Amount Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(S_YELLOW, RoundedCornerShape(8.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total Amount",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                        )
                        Text(
                            text = "LKR 2500.00",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Choose Payment Method Section
                Text(
                    text = "Choose Payment Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Corrected usage: pass the drawable resource ID (Int)
                PaymentMethodOption(
                    iconRes = R.drawable.dollar_sign,
                    title = "Cash Payment",
                    description = "Pay directly to service provider",
                    feeInfo = "No processing fees",
                    isSelected = selectedPaymentMethod == PaymentMethod.Cash,
                    onClick = { selectedPaymentMethod = PaymentMethod.Cash }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // For Credit/Debit Card: Using your custom drawable (e.g., credit_card.xml)
                // Corrected usage: pass the drawable resource ID (Int)
                PaymentMethodOption(
                    iconRes = R.drawable.credit_card,
                    title = "Credit/Debit Card",
                    description = "Visa, Mastercard, American Express",
                    feeInfo = "No processing fees",
                    isSelected = selectedPaymentMethod == PaymentMethod.CreditDebitCard,
                    onClick = { selectedPaymentMethod = PaymentMethod.CreditDebitCard }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Credit Details Section (conditionally visible)
                if (selectedPaymentMethod == PaymentMethod.CreditDebitCard) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Credit Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it.filter { char -> char.isDigit() || char == ' ' } }, // Basic filtering
                            label = { Text("Card Number", style = MaterialTheme.typography.labelLarge) },
                            placeholder = { Text("1234 5678 9012 3456", style = MaterialTheme.typography.bodyMedium.copy(color = S_PLACEHOLDER)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary, // S_YELLOW
                                unfocusedBorderColor = S_STROKE_COLOR,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            isError = cardNumberError != null,
                            supportingText = cardNumberError?.let { { Text(it) } }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { input ->
                                    if (input.length <= 5) {
                                        expiryDate = formatExpiryDate(input)
                                    }
                                },
                                label = { Text("Expiry Date", style = MaterialTheme.typography.labelLarge) },
                                placeholder = { Text("MM/YY", style = MaterialTheme.typography.bodyMedium.copy(color = S_PLACEHOLDER)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary, // S_YELLOW
                                    unfocusedBorderColor = S_STROKE_COLOR,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                isError = expiryDateError != null,
                                supportingText = expiryDateError?.let { { Text(it) } }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { if (it.length <= 4) cvv = it.filter { char -> char.isDigit() } }, // CVV usually 3 or 4 digits
                                label = { Text("CVV", style = MaterialTheme.typography.labelLarge) },
                                placeholder = { Text("123", style = MaterialTheme.typography.bodyMedium.copy(color = S_PLACEHOLDER)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary, // S_YELLOW
                                    unfocusedBorderColor = S_STROKE_COLOR,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                isError = cvvError != null,
                                supportingText = cvvError?.let { { Text(it) } }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = cardholderName,
                            onValueChange = { input ->
                                // Only allow letters, spaces, hyphens, and apostrophes
                                cardholderName = input.filter { 
                                    it.isLetter() || it.isWhitespace() || it == '-' || it == '/'
                                }
                            },
                            label = { Text("Cardholder Name", style = MaterialTheme.typography.labelLarge) },
                            placeholder = { Text("Enter your name", style = MaterialTheme.typography.bodyMedium.copy(color = S_PLACEHOLDER)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary, // S_YELLOW
                                unfocusedBorderColor = S_STROKE_COLOR,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            isError = cardholderNameError != null,
                            supportingText = cardholderNameError?.let { { Text(it) } }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SSL Encryption Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(S_BLUE_BACKGROUND, RoundedCornerShape(8.dp)) // Light blue background from your colors
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.shield), // CORRECTED LINE: Use painterResource for drawable IDs
                        contentDescription = "SSL Encrypted",
                        tint = S_BLUE, // Use your S_BLUE
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "256-bit SSL Encryption",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        )
                        Text(
                            text = "Your payment information is protected and secure",
                            style = MaterialTheme.typography.bodySmall.copy(color = S_LIGHT_TEXT)
                        )
                    }
                }
            }

            // Loading indicator overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = S_YELLOW)
                }
            }

            // Error message
            state.error?.let { error ->
                LaunchedEffect(error) {
                    // Show snackbar or dialog
                }
            }
        }
    }
}

@Composable
fun PaymentMethodOption(
    iconRes: Int,
    title: String,
    description: String,
    feeInfo: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Determine colors based on selection and payment method type
    val borderColor = if (isSelected) {
        when (title) {
            "Cash Payment" -> S_GREEN
            "Credit/Debit Card" -> S_BLUE
            else -> S_BLUE
        }
    } else {
        null
    }

    val iconTint = if (title == "Cash Payment") S_GREEN else S_BLUE
    val selectedCheckmarkTint = if (title == "Cash Payment") S_GREEN else S_BLUE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = borderColor?.let { BorderStroke(2.dp, it) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = S_LIGHT_TEXT))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Check",
                        tint = S_GREEN,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = feeInfo, style = MaterialTheme.typography.bodySmall.copy(color = S_GREEN))
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = selectedCheckmarkTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

enum class PaymentMethod {
    Cash,
    CreditDebitCard
}
data class CardDetails(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val cardholderName: String
)

