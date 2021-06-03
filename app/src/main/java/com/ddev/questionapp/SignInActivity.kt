package com.ddev.questionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        signin_btn_login.setOnClickListener {
            val email: String = email_edit_text.text.toString().trim()
            val password: String = password_edit_text.text.toString().trim()
            signInUser(email,password)
        }

        signup_btn_login.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "login", Toast.LENGTH_SHORT).show()
                    Log.d("login", it.result.toString())
                    val intent = Intent(this,QuestionActivity::class.java)
                    startActivity(intent)
                }else{
                    Log.d("login failed", it.exception.toString())
                }
            }
    }
}