package com.example.ecostore

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecostore.common.Common
import com.example.ecostore.model.UserModel
import com.example.ecostore.remote.CloudFuncions
import com.example.ecostore.remote.RetrofitCloudClient
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.MaskTextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var dialog: android.app.AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunction: CloudFuncions

    private lateinit var userRef: DatabaseReference
    private var providers:List<AuthUI.IdpConfig>? = null

    companion object {
        private const val APP_REQUEST_CODE = 7171
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (listener != null) {
            firebaseAuth.removeAuthStateListener(listener)
            compositeDisposable.clear()
        }

        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        providers = listOf(AuthUI.IdpConfig.PhoneBuilder().build())
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        cloudFunction = RetrofitCloudClient.getInstance().create(CloudFuncions::class.java)
        listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                checkUserFromFirebase(user)
            } else {
                    phoneLogin()
            }
        }
    }

    private fun checkUserFromFirebase(user: FirebaseUser) {
        dialog.show()
        userRef.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity, "" + p0.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()){
                        val userModel = dataSnapshot.getValue(UserModel::class.java)
                        goToHomeActivity(userModel)
                    } else {
                        showRegisterDialog(user)
                    }

                    dialog.dismiss()
                }

            })
    }

    private fun showRegisterDialog(user: FirebaseUser) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Registro")
        builder.setMessage("Por favor preencha as informações")

        val itemView = LayoutInflater.from(this@MainActivity)
            .inflate(R.layout.layout_register, null)

        val nameEdt = itemView.findViewById<EditText>(R.id.edt_name)
        val addressEdt = itemView.findViewById<EditText>(R.id.edt_address)
        val phoneEdt = itemView.findViewById<EditText>(R.id.edt_phone)
        val edtEmail = itemView.findViewById<EditText>(R.id.edt_email)


        //MASKS
        val smfPhone = SimpleMaskFormatter("(NN)NNNNN-NNNN")
        val smfCEP = SimpleMaskFormatter("NN.NNN-NNNN")

        val smwPhone = MaskTextWatcher(phoneEdt, smfPhone)
        val smwCEP = MaskTextWatcher(addressEdt, smfCEP)

        phoneEdt.addTextChangedListener(smwPhone)
        addressEdt.addTextChangedListener(smwCEP)

        phoneEdt.setText(user.phoneNumber.toString())

        builder.setView(itemView)
        builder.setNegativeButton("CANCELAR") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.setNegativeButton("CADASTRAR") { dialogInterface, _ ->
            if (TextUtils.isDigitsOnly(nameEdt.text.toString())){
                Toast.makeText(this@MainActivity, "Por favor insira seu nome", Toast.LENGTH_SHORT).show()
                return@setNegativeButton
            } else if (TextUtils.isDigitsOnly(addressEdt.toString())){
                Toast.makeText(this@MainActivity, "Por favor insira seu endereço", Toast.LENGTH_SHORT).show()
                return@setNegativeButton
            }

            val userModel = UserModel()
            userModel.uid = user.uid
            userModel.name = nameEdt.text.toString()
            userModel.address = addressEdt.text.toString()
            userModel.phone = phoneEdt.text.toString()
            userModel.email = edtEmail.text.toString()

            userRef.child(user.uid)
                .setValue(userModel)
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        dialogInterface.dismiss()
                        Toast.makeText(this@MainActivity, "Registro completo com sucesso", Toast.LENGTH_SHORT).show()
                        goToHomeActivity(userModel)
                    }
                }

        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun goToHomeActivity(userModel: UserModel?) {
        Common.currentUser = userModel
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        finish()
    }

    private fun phoneLogin() {

        startActivityForResult(
            providers?.let {
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(it)
                    .build()}, APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Falha ao entrar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
