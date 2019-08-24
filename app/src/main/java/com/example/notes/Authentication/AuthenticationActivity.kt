package com.example.notes.Authentication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.notes.MainActivity
import com.example.notes.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.auth_activity.*

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var login:LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)

        val buttonSignIn= findViewById<Button>(R.id.btn_sign_in)
        val buttonSignUp = findViewById<Button>(R.id.btn_sign_up)

        auth = FirebaseAuth.getInstance()

        login=object:LoginManager(auth){
            override fun isSuccessLogin() {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

            override fun isFailedLogin() {
                Log.i("Access failed","Access failed")
            }

        }

        buttonSignIn.setOnClickListener(View.OnClickListener { v ->
            if(!auth_login_edit_text.text.isEmpty() && !auth_password_edit_text.text.isEmpty()) {
                login.go(auth_login_edit_text.text.toString(),auth_password_edit_text.text.toString())
            }else{
                Snackbar.make(getWindow().currentFocus, "Please enter login or password!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .show()
            }
        })

        buttonSignUp.setOnClickListener(View.OnClickListener { v ->
            if(!auth_login_edit_text.text.isEmpty() && !auth_password_edit_text.text.isEmpty()) {
                signUp(auth_login_edit_text.text.toString(), auth_password_edit_text.text.toString())
            }else{
                Snackbar.make(getWindow().currentFocus, "Please enter login or password!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .show()
            }        })

    }

    override fun onStart() {
        super.onStart()
        login.onStart()
    }

    override fun onStop() {
        super.onStop()
        login.onStop()
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