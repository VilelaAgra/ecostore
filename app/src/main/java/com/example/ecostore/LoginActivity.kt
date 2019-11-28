package com.example.projetoandroidmobile.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ecostore.HomeActivity
import com.example.ecostore.R
import com.example.projetoandroidmobile.utils.isEmptyField
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //Constant
    private val TAG = "LoginActivity"

    //Global Variables
    private lateinit var email: String
    private lateinit var password: String

    //UI elements
    private lateinit var textViewForgotPassword: TextView

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var buttonLogin: Button
    private lateinit var buttonCreateAccount: Button

    private lateinit var checkBoxRememberPassword: CheckBox

    private lateinit var mProgressBar: ProgressDialog

    // Database references
    private lateinit var mAuthentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupLoginComponents()
    }

    private fun setupLoginComponents() {
        setupTextView()
        setupEditText()
        setupLoginButton()
        setupCreateNewAccountButton()
        setupCheckbox()
        setupProgressBar()
        setupDataBase()
    }

    private fun setupDataBase() {
        mAuthentication = FirebaseAuth.getInstance()
    }

    private fun setupProgressBar() {
        mProgressBar = ProgressDialog(this)
    }

    private fun setupCheckbox() {
        checkBoxRememberPassword = findViewById(R.id.login_checkbox)
    }

    private fun setupCreateNewAccountButton() {
        buttonCreateAccount = findViewById(R.id.login_button_create_account)
        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }
    }

    private fun setupLoginButton() {
        buttonLogin = findViewById(R.id.login_button)
        buttonLogin.setOnClickListener {
            if (validateFields()) {
                loginUser()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.loginError),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupEditText() {
        etEmail = findViewById(R.id.login_edit_text_account_email)
        etPassword = findViewById(R.id.login_edit_text_password)
    }

    private fun setupTextView() {
        textViewForgotPassword = findViewById(R.id.login_text_view_forgot_password)
        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    //Change the color of topbar on Android
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Window.setStatusBarColorTo(color: Int) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = ContextCompat.getColor(baseContext, color)
    }

    private fun loginUser() {
        authentication()
    }

    private fun validateFields(): Boolean {
        email = etEmail.text.toString()
        password = etPassword.text.toString()
        return !isEmptyField(email, etEmail) && !isEmptyField(password, etPassword)
    }

    private fun authentication() {
        mAuthentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    Log.d(TAG, "Autenticado com sucesso!")
                    goToHomeActivity()
                } else {
                    Log.d(TAG, "Erro ao autenticar", task.exception)
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro ao atenticar usuário",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun hideProgressDialog() {
        mProgressBar.hide()
    }

    private fun showProgressDialog() {
    }


}
