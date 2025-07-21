package com.sevalk.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sevalk.R
import timber.log.Timber
import kotlinx.coroutines.tasks.await

class GoogleSignInHelper(private val context: Context) {
    
    suspend fun getSignInIntent(): Intent {
        // Create GoogleSignInOptions without filtering by account
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        val client = GoogleSignIn.getClient(context, gso)
        
        try {
            // Check if there's an existing signed-in account before clearing
            val existingAccount = GoogleSignIn.getLastSignedInAccount(context)
            if (existingAccount != null) {
                // Sign out and revoke access to force account selection
                client.signOut().await()
                client.revokeAccess().await()
                Timber.d("Successfully cleared existing Google Sign-In session")
            } else {
                Timber.d("No existing Google Sign-In session to clear")
            }
        } catch (e: Exception) {
            // Log as debug instead of warning since this is expected when no user is signed in
            Timber.d(e, "No existing Google Sign-In session or failed to clear cache (this is normal)")
        }
        
        return client.signInIntent
    }
    
    fun handleSignInResult(task: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return try {
            val account = task.getResult(ApiException::class.java)
            Timber.d("Google Sign-In successful: ${account?.email}")
            account
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In failed with status code: ${e.statusCode}")
            null
        }
    }
    
    suspend fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        val client = GoogleSignIn.getClient(context, gso)
        try {
            // Check if there's an existing signed-in account before signing out
            val existingAccount = GoogleSignIn.getLastSignedInAccount(context)
            if (existingAccount != null) {
                client.signOut().await()
                client.revokeAccess().await()
                Timber.d("Successfully signed out from Google")
            } else {
                Timber.d("No Google account to sign out from")
            }
        } catch (e: Exception) {
            Timber.d(e, "No existing Google Sign-In session or failed to sign out (this is normal)")
        }
    }
}
