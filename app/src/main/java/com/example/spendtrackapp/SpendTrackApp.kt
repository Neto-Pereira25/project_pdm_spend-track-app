package com.example.spendtrackapp

import android.app.Application
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SpendTrackApp : Application() {

    private val flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TASK

    override fun onCreate() {
        super.onCreate()

        Firebase.auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                goToMain()
            } else {
                goToLogin()
            }
        }
    }

    private fun goToMain() {
        startActivity(
            Intent(this, MainActivity::class.java).setFlags(flags)
        )
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).setFlags(flags)
        )
    }
}
