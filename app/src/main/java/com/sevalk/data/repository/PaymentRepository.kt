package com.sevalk.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.models.Payment
import com.sevalk.data.models.PaymentStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createPayment(payment: Payment): Result<Payment> {
        return try {
            val paymentRef = firestore.collection("payments").document()
            val paymentWithId = payment.copy(id = paymentRef.id)
            
            paymentRef.set(paymentWithId).await()
            Result.success(paymentWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePaymentStatus(paymentId: String, status: PaymentStatus): Result<Unit> {
        return try {
            firestore.collection("payments")
                .document(paymentId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPayment(paymentId: String): Result<Payment> {
        return try {
            val snapshot = firestore.collection("payments")
                .document(paymentId)
                .get()
                .await()
            
            val payment = snapshot.toObject(Payment::class.java)
            if (payment != null) {
                Result.success(payment)
            } else {
                Result.failure(Exception("Payment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
