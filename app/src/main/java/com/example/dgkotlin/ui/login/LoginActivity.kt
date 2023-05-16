package com.example.dgkotlin.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.dgkotlin.BuildConfig
import com.example.dgkotlin.NavActivity
import com.example.dgkotlin.databinding.ActivityLoginBinding

import com.example.dgkotlin.R
import com.example.dgkotlin.data.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

//    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val remember = binding.rememberMe

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(applicationContext))[LoginViewModel::class.java]

        val prefs = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        if (prefs != null) {
            username.setText(prefs.getString(BuildConfig.USERNAME_PREF_KEY, ""))
        }
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.legacyResult.observe(this@LoginActivity, Observer {
            val legacyResult = it ?: return@Observer
            if (legacyResult.success != null) {
                storeLegacyLoginToken(legacyResult.success, remember.isChecked)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success, remember.isChecked)

                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
                finish()
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun storeLegacyLoginToken(model: User, remember: Boolean) {
        getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            .edit()
            .putString(BuildConfig.JWT_LEGACY_TOKEN_PREF_KEY, model.token)
            .apply()
    }

    private fun updateUiWithUser(model: User, remember: Boolean) {
        val welcome = getString(R.string.welcome)
        val displayName = model.username

        val prefs: SharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        with (prefs.edit()) {
            putString(BuildConfig.JWT_TOKEN_PREF_KEY, model.token)
            model.id?.let { putLong(BuildConfig.USER_ID_PREF_KEY, it) }
            if (remember) putString(BuildConfig.USERNAME_PREF_KEY, model.username)
            putString(BuildConfig.REMEMBER_PREF_KEY, remember.toString())
            apply()
        }
        val navIntent = Intent(this@LoginActivity, NavActivity::class.java)
        startActivity(navIntent)

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}