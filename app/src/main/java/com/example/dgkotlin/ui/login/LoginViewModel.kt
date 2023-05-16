package com.example.dgkotlin.ui.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.dgkotlin.R
import com.example.dgkotlin.data.model.User
import com.example.dgkotlin.data.remote.request.LoginRequest
import com.example.dgkotlin.data.remote.service.LegacyUserService
import com.example.dgkotlin.data.remote.service.UserService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.log

class LoginViewModel(
    private val userService: UserService,
    private val legacyService: LegacyUserService
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _legacyResult = MutableLiveData<LoginResult>()
    var legacyResult: LiveData<LoginResult> = _legacyResult

    @SuppressLint("CheckResult")
    fun login(username: String, password: String) {
        val req = LoginRequest(username, password)
        legacyService.login(req)
            .map { result -> result.user }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                user -> _legacyResult.value = LoginResult(success = user)
                Log.d("OkHttp - LegacyResult", user.toString())
            }
        userService.login(req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    user -> _loginResult.value = LoginResult(success = user)
                    Log.d("OkHttp - Result", user.toString())
                },
                {
                    error -> _loginResult.value = LoginResult(error = R.string.login_failed)
                    Log.d("OkHttp - Error", error.toString())
                }
            )
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
