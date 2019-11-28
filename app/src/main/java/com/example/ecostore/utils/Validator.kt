package com.example.projetoandroidmobile.utils

import android.text.TextUtils
import android.widget.EditText

fun isEmptyField (value: String, et: EditText): Boolean {
    return if (TextUtils.isEmpty(value) || value.trim().isEmpty()) {
        et.requestFocus()
        et.error = "Campo Obrigatório"
        true
    } else {
        false
    }
}

fun isPasswordValid(password: String, etPassword: EditText): Boolean {
    return if (TextUtils.isEmpty(password) || password.trim().isEmpty()) {
        etPassword.requestFocus()
        etPassword.error = "A senha deve ter pelo menos 6 carácteres."
        true
    } else {
        false
    }
}