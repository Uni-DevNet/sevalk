package com.sevalk.di


import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.repository.PaymentRepository
import dagger.Provides
import com.sevalk.data.repositories.BookingRepository
import com.sevalk.data.repositories.BookingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    
    @Provides
    @Singleton
    fun providePaymentRepository(
        firestore: FirebaseFirestore
    ): PaymentRepository {
        return PaymentRepository(firestore)
    }
    

}

abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
}

