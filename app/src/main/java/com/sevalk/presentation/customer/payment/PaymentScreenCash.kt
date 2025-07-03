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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle // Needed for PaymentMethodOption
import androidx.compose.material.icons.filled.Info // For the Cash Instructions card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sevalk.R
import com.sevalk.ui.theme.S_BLUE // Needed for PaymentMethodOption and its borders
import com.sevalk.ui.theme.S_GREEN
import com.sevalk.ui.theme.S_GREEN_BACKGROUND
import com.sevalk.ui.theme.S_INPUT_BACKGROUND
import com.sevalk.ui.theme.S_LIGHT_TEXT
import com.sevalk.ui.theme.S_YELLOW
import com.sevalk.ui.theme.SevaLKTheme

// Enum for Payment Methods (Re-added, as it's crucial for PaymentMethodOption)
enum class CashPaymentMethod {
    Cash,
    CreditDebitCard
}

// Data class for Card Details (Not strictly used in CashPaymentScreen, but good to keep if from a shared context)
data class CashDetails(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val cardholderName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashPaymentScreen( // Renamed to ensure it's distinct if PaymentScreen also exists
    onBackClick: () -> Unit = {},
    onConfirmPayment: (paymentMethod: PaymentMethod, cardDetails: CardDetails?) -> Unit = { _, _ -> }
) {
    // Define selectedPaymentMethod here, initialized to Cash to match the picture
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod>(PaymentMethod.Cash) }

    // Card details are not directly used on this dedicated cash screen, but kept for method signature consistency
    val cardNumber by remember { mutableStateOf("") }
    val expiryDate by remember { mutableStateOf("") }
    val cvv by remember { mutableStateOf("") }
    val cardholderName by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Empty title as per screenshot
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(id = R.drawable.arrow_left), contentDescription = "Back",
                            modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // White background for the top app bar
                ),
                modifier = Modifier.height(48.dp)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val cardDetails = if (selectedPaymentMethod == PaymentMethod.CreditDebitCard) {
                            CardDetails(cardNumber, expiryDate, cvv, cardholderName)
                        } else null
                        onConfirmPayment(selectedPaymentMethod, cardDetails) // Use the general onConfirmPayment
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = S_YELLOW),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Conditional Icon based on selectedPaymentMethod for the bottom button
                        val buttonIcon = if (selectedPaymentMethod == PaymentMethod.Cash) {
                            R.drawable.dollar_sign // Dollar sign for cash payment
                        } else {
                            R.drawable.credit_card // Credit card for other payments
                        }
                        Icon(
                            painter = painterResource(id = buttonIcon),
                            contentDescription = "Payment Button Icon",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            // Conditional Text based on selectedPaymentMethod for the bottom button
                            text = if (selectedPaymentMethod == PaymentMethod.Cash) "Confirm Cash Payment" else "Pay LKR 2500.00",
                            color = MaterialTheme.colorScheme.onPrimary,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(S_INPUT_BACKGROUND)
                .padding(paddingValues)
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

            // Choose Payment Method Section (Moved outside the Box)
            Text(
                text = "Choose Payment Method",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Cash Payment Option
            PaymentMethodOption(
                iconRes = R.drawable.dollar_sign,
                title = "Cash Payment",
                description = "Pay directly to service provider",
                feeInfo = "No processing fees",
                isSelected = selectedPaymentMethod == PaymentMethod.Cash,
                onClick = { selectedPaymentMethod = PaymentMethod.Cash }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Credit/Debit Card Option
            PaymentMethodOption(
                iconRes = R.drawable.credit_card,
                title = "Credit/Debit Card",
                description = "Visa, Mastercard, American Express",
                feeInfo = "No processing fees",
                isSelected = selectedPaymentMethod == PaymentMethod.CreditDebitCard,
                onClick = { selectedPaymentMethod = PaymentMethod.CreditDebitCard }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Conditionally display content based on selected payment method
            when (selectedPaymentMethod) {
                PaymentMethod.Cash -> {
                    CashInstructionsCard()
                }
                PaymentMethod.CreditDebitCard -> {
                    // Credit Details Section
                    // This part would typically be rendered if CreditDebitCard was selected.
                    // For the CashPaymentScreen specifically matching the image, this section is omitted.
                    // If you were using the more general PaymentScreen, you'd include it here.
                    // ... (Credit card input fields and SSL encryption info if needed)
                }
            }
        }
    }
}

// Re-using helper composables from the main PaymentScreen
@Composable
fun CashInstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = S_GREEN_BACKGROUND), // Light green background
        border = BorderStroke(1.dp, S_GREEN) // Green border
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info, // Info icon
                    contentDescription = "Cash Payment Instructions",
                    tint = S_GREEN,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cash Payment Instructions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = S_GREEN)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                CashInstructionPoint("Have exact amount ready: LKR 2500.00")
                CashInstructionPoint("Payment due upon service completion")
                CashInstructionPoint("Receipt will be provided by the service provider")
            }
        }
    }
}

@Composable
fun CashInstructionPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "•", // Bullet point
            style = MaterialTheme.typography.bodyMedium.copy(color = S_GREEN, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

// PaymentMethodOption (Re-added as it's needed for the "Choose Payment Method" section)
@Composable
fun PaymentMethodOptionCash(
    iconRes: Int, // Drawable resource ID
    title: String,
    description: String,
    feeInfo: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Determine colors based on selection and payment method type
    val borderColor = if (isSelected) {
        when (title) { // Assuming "Cash Payment" for green border, "Credit/Debit Card" for blue border
            "Cash Payment" -> S_GREEN
            "Credit/Debit Card" -> S_BLUE
            else -> S_BLUE // Default or other types
        }
    } else {
        null // No border when not selected
    }

    val iconTint = if (title == "Cash Payment") S_GREEN else S_BLUE // Green for cash, blue for card
    val selectedCheckmarkTint = if (title == "Cash Payment") S_GREEN else S_BLUE // Green for cash check, blue for card check

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = borderColor?.let { BorderStroke(2.dp, it) }, // Apply border if it's not null
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon from drawable resource
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint, // Apply conditional tint
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
                        tint = S_GREEN, // Fee info checkmark is always green
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
                    tint = selectedCheckmarkTint, // Apply conditional tint for selected checkmark
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CashPaymentScreenPreview() {
    SevaLKTheme {
        CashPaymentScreen()
    }
}