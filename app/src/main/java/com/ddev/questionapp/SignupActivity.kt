package com.ddev.questionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignupActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        signup_btn.setOnClickListener {
            val userName: String = name_edit_text.text.toString().trim()
            val email: String = email_edit_text.text.toString().trim()
            val password: String = password_edit_text.text.toString().trim()
            if (userName.isEmpty()) {
                name_edit_text.error = "Please enter your name"
                name_edit_text.requestFocus()
            } else if (email.isEmpty()) {
                email_edit_text.error = "Please enter your email"
                email_edit_text.requestFocus()
            } else if (password.isEmpty()) {
                password_edit_text.error = "Please enter your password"
                password_edit_text.requestFocus()
            } else if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signUpUser(userName, email, password)
            }
            //signUpUser(userName, email, password)
        }
        signin_btn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser(userName: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        val uId: String = mAuth.uid.toString()
                        val userData = UserData(userName, uId, email, password)

                        db.collection("UserData")
                                .add(userData)
                                .addOnSuccessListener { data ->
                                    Log.d("details saved in db", "$data")
                                }
                        val intent = Intent(this, QuestionActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "account created", Toast.LENGTH_SHORT).show()
                        Log.d("account created", it.result.toString())
                    } else {
                        Log.d("account not created", it.exception.toString())
                    }
                }
    }
}