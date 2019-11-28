package com.example.projetoandroidmobile.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecostore.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    //Constant
    private val TAG = "ForgotPasswordActivity"

    //UI Elements
    private lateinit var etEmail: EditText
    private lateinit var buttonSubmit: Button

    //Database References

    private lateinit var mAuthentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setupForgotPasswordComponents()
    }

    private fun setupForgotPasswordComponents() {
        setupEditText()
        setupSubmitButton()
        setupDatabase()
    }

    private fun setupDatabase() {
        mAuthentication = FirebaseAuth.getInstance()
    }

    private fun setupEditText() {
        etEmail = findViewById(R.id.forgot_edit_text)
    }

    private fun setupSubmitButton() {
        buttonSubmit = findViewById(R.id.forgot_button)
        buttonSubmit.setOnClickListener {
            sendPasswordEmail()
        }
    }

    private fun sendPasswordEmail() {
        val email = etEmail.text.toString()
        if (!TextUtils.isEmpty(email)) {
            setupAuthentication(email)
        } else {
            Toast.makeText(this, "Insira um e-mail válido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAuthentication(email: String) {
        mAuthentication.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val message = "E-mail enviado."
                Log.d(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                goToLogin()
            } else {
                val message = "Endereço de e-mail não encontrado."
                Log.w(TAG, message, task.exception)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
