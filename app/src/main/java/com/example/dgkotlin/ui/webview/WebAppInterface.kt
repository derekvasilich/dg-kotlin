package com.example.dgkotlin.ui.webview

import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.dgkotlin.ui.login.LoginActivity


class WebAppInterface(private var mContext: Context? = null) {

    @JavascriptInterface
    fun showToast( toast: String ) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun logout() {
        val intent: Intent = Intent(mContext, LoginActivity::class.java)
        mContext?.startActivity(intent)
    }

}