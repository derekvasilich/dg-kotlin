package com.example.dgkotlin.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dg_andriod.data.remote.ApiUtils

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    private val _context: Context = context;

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                userService = ApiUtils.getUserService(_context),
                legacyService = ApiUtils.getLegacyUserService(_context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}