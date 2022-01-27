package com.example.binder.ui

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class GoogleAccountProvider(private val context: Context){

    companion object {
        private const val SCOPE = "oauth2:${DriveScopes.DRIVE}"
    }

    private var googleAccountSignIn: GoogleSignInAccount? = null

    fun tryGetAccount(): GoogleSignInAccount? {
        if (googleAccountSignIn == null) {
            googleAccountSignIn = GoogleSignIn.getLastSignedInAccount(context)
        }
        return googleAccountSignIn
    }

    fun tryGetDriveService(): Drive? {
        googleAccountSignIn?.let {
            it.account?.let { account ->
                val credential = GoogleCredential().setAccessToken(GoogleAuthUtil.getToken(context, account, SCOPE))
                return Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("com.example.binder").build()
            }
        }
        return null
    }
}
