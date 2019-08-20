package com.example.notes.Authentication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.example.notes.MainActivity
import com.example.notes.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.auth_activity.*

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)

        auth = FirebaseAuth.getInstance()

        val buttonSignIn= findViewById<Button>(R.id.btn_sign_in)
        val buttonSignUp = findViewById<Button>(R.id.btn_sign_up)

        buttonSignIn.setOnClickListener(View.OnClickListener { v ->
            signIn(auth_login_edit_text.text.toString(), auth_password_edit_text.text.toString())
        })

        buttonSignUp.setOnClickListener(View.OnClickListener { v ->
            signUp(auth_login_edit_text.text.toString(), auth_password_edit_text.text.toString())
        })

    }

    private fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(getWindow().currentFocus, "Access denied!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .show()
                }
            })
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Snackbar.make(getWindow().currentFocus, "Registration denied!", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .show()
                }
            })
    }
}