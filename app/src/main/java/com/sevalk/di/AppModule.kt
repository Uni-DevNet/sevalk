package com.sevalk.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.sevalk.data.repositories.AuthRepository
import com.sevalk.data.repositories.AuthRepositoryImpl
import com.sevalk.data.repositories.BookingRepository
import com.sevalk.data.repositories.BookingRepositoryImpl
import com.sevalk.data.repositories.ChatRepository
import com.sevalk.data.repositories.ChatRepositoryImpl
import com.sevalk.data.repositories.ImageRepository
import com.sevalk.data.repositories.NotificationRepository
import com.sevalk.data.repositories.NotificationRepositoryImpl
import com.sevalk.utils.GoogleSignInHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        firestore: FirebaseFirestore,
        imageRepository: ImageRepository
    ): ChatRepository {
        return ChatRepositoryImpl(auth, database, firestore, imageRepository)
    }
    
    @Provides
    @Singleton
    fun provideGoogleSignInHelper(@ApplicationContext context: Context): GoogleSignInHelper {
        return GoogleSignInHelper(context)
    }
}