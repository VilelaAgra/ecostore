package com.example.projetoandroidmobile.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecostore.R
import com.example.projetoandroidmobile.utils.isEmptyField
import com.example.projetoandroidmobile.utils.isPasswordValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    //Layout inputs references
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var buttonCreateAccount: Button
    private lateinit var mProgressBar: ProgressDialog

    //Database references
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuthentication: FirebaseAuth

    //Constant
    private val TAG = "CreateAccountActivity"

    //Global variables
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        initComponents()
    }

    private fun initComponents() {
        setupDataBase()
        setupEditTexts()
        setupProgressDialog()
        setupButton()
    }

    private fun setupDataBase() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase.reference.child("Users")
        setupDatabase()
    }

    private fun setupDatabase() {
        mAuthentication = FirebaseAuth.getInstance()
    }

    private fun setupProgressDialog() {
        mProgressBar = ProgressDialog(this)
    }

    private fun setupEditTexts() {
        etFirstName = findViewById(R.id.create_edit_text_first_name)
        etLastName = findViewById(R.id.create_edit_text_last_name)
        etEmail = findViewById(R.id.create_edit_text_email)
        etPassword = findViewById(R.id.create_edit_text_password)
    }

    private fun validateFields(): Boolean {
        firstName = etFirstName.text.toString()
        lastName = etLastName.text.toString()
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        return !isEmptyField(firstName, etFirstName) && !isEmptyField(lastName, etLastName) &&
                !isEmptyField(email, etEmail) && !isPasswordValid(password, etPassword)
    }

    private fun isEmpty(value: String, et: EditText): Boolean {
        return if (TextUtils.isEmpty(value) || value.trim().isEmpty()) {
            et.requestFocus()
            et.error = getString(R.string.required)
            true
        } else {
            false
        }
    }

    private fun setupButton() {
        buttonCreateAccount = findViewById(R.id.create_button_create_account)
        buttonCreateAccount.setOnClickListener {
            if (validateFields()) {
                createNewAccount()
            } else {
                Toast.makeText(
                    this@CreateAccountActivity,
                    getString(R.string.createFail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createNewAccount() {
        showProgressDialog()
        setupAuthentication()
    }

    private fun setupAuthentication() {
        mAuthentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    Log.d(TAG, "CreateUserWithEmailAndPassword")

                    val userId = mAuthentication.currentUser?.uid

                    verifyEmail()

                    val currentUserDb = mDatabaseReference.child(userId.toString())
                    currentUserDb.child("firstName").setValue(firstName)
                    currentUserDb.child("lastName").setValue(lastName)

                    goToHomeActivity()

                } else {
                    Log.e(TAG, "CreateUserWithEmail:Failure", task.exception)
                    Toast.makeText(
                        this@CreateAccountActivity,
                        getString(R.string.failAuth),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun verifyEmail() {
        //Verify
        val mUser = mAuthentication.currentUser
        mUser?.sendEmailVerification()?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@CreateAccountActivity,
                    getString(R.string.verifySendTo) + mUser.email,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@CreateAccountActivity,
                    getString(R.string.verifySendFail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this@CreateAccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun hideProgressDialog() {
        mProgressBar.hide()
    }

    private fun showProgressDialog() {
        mProgressBar.setMessage("Registrando Usuário...")
        mProgressBar.show()
    }

}
