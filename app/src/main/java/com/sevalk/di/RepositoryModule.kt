package com.sevalk.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.api.PaymentApiService
import com.sevalk.data.repositories.PaymentRepository
import com.sevalk.data.repositories.PaymentRepositoryImpl
import com.sevalk.data.repositories.BookingRepository
import com.sevalk.data.repositories.BookingRepositoryImpl
import com.sevalk.data.repositories.UserRepository
import com.sevalk.data.repositories.UserRepositoryImpl
import com.sevalk.data.repositories.AuthRepository
import com.sevalk.data.repositories.NotificationRepository
import com.sevalk.data.repositories.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
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

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
}

