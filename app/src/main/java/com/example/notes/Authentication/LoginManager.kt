package com.example.notes.Authentication

import com.google.firebase.auth.FirebaseAuth

abstract class LoginManager(val mAuth: FirebaseAuth)  {

    val mAuthListener = FirebaseAuth.AuthStateListener({ firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) isSuccessLogin()
    })

    abstract fun isSuccessLogin()

    fun go(email:String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { complete ->
                if (!complete.isSuccessful) isFailedLogin()
            }
    }

    abstract fun isFailedLogin()

    fun onStart() {
        mAuth.addAuthStateListener(mAuthListener)
    }

    fun onStop() {
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}