package com.sevalk.domain.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.models.CardDetails
import com.sevalk.data.models.Payment
import com.sevalk.data.models.PaymentDetails
import com.sevalk.data.models.PaymentMethodType
import com.sevalk.data.models.PaymentStatus
import com.sevalk.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    fun processPayment(
        amount: Double,
        paymentMethod: PaymentMethodType,
        cardDetails: CardDetails? = null,
        bookingId: String,
        customerId: String,
        providerId: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val payment = Payment(
                    bookingId = bookingId,
                    customerId = customerId,
                    providerId = providerId,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    paymentDetails = cardDetails?.let { PaymentDetails(cardDetails = it) },
                    status = PaymentStatus.PROCESSING
                )

                val result = paymentRepository.createPayment(payment)
                
                result.onSuccess { savedPayment ->
                    // Simulate payment processing
                    kotlinx.coroutines.delay(2000)
                    
                    // Update payment status
                    paymentRepository.updatePaymentStatus(savedPayment.id, PaymentStatus.COMPLETED)
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        paymentSuccess = true,
                        payment = savedPayment.copy(status = PaymentStatus.COMPLETED)
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Payment failed"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Payment failed"
                )
            }
        }
    }
}

data class PaymentState(
    val isLoading: Boolean = false,
    val paymentSuccess: Boolean = false,
    val payment: Payment? = null,
    val error: String? = null
)
