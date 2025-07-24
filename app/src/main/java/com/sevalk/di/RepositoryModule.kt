package com.sevalk.di


import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.repositories.PaymentRepository
import dagger.Provides
import com.sevalk.data.repositories.BookingRepository
import com.sevalk.data.repositories.BookingRepositoryImpl
import com.sevalk.data.repositories.UserRepository
import com.sevalk.data.repositories.UserRepositoryImpl
import com.sevalk.data.repositories.AuthRepository
import com.sevalk.data.repositories.NotificationRepository
import com.sevalk.data.repositories.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun providePaymentRepository(
            firestore: FirebaseFirestore
        ): PaymentRepository {
            return PaymentRepository(firestore)
        }
    }
}

