package com.example.dgkotlin.ui.login

import com.example.dgkotlin.data.model.User

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: Int? = null
)