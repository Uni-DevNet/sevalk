package com.sevalk.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sevalk.data.repository.PaymentRepository
import dagger.Module
import dagger.Provides
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

