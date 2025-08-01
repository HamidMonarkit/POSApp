package com.example.posapp.utils


object Validator {
    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=\\-]).{8,}$")

    fun isPasswordStrong(password: String): Boolean {
        return passwordRegex.matches(password)
    }
}
