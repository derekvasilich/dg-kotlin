package com.example.dgkotlin.ui.webview

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.example.dgkotlin.BuildConfig

class WebViewClientImpl(
    private val activity: FragmentActivity
) : WebViewClient() {

    private val prefs: SharedPreferences = activity.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    private val jwtToken: String? = prefs.getString(BuildConfig.JWT_TOKEN_PREF_KEY, null)
    private val legacyToken: String? = prefs.getString(BuildConfig.JWT_LEGACY_TOKEN_PREF_KEY, null)

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view!!.evaluateJavascript(String.format("sessionStorage.setItem(\"%s\", \"%s\")", BuildConfig.JWT_TYPE_PREF_KEY, "Bearer"), null);
        view.evaluateJavascript(String.format("sessionStorage.setItem(\"%s\", \"%s\")", BuildConfig.JWT_TOKEN_PREF_KEY, jwtToken), null);
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request!!.url.toString()
        if (url.indexOf("10.0.2.2") > -1) {
            val additionalHttpHeaders: MutableMap<String, String> = HashMap()
            if (legacyToken != null) additionalHttpHeaders["X-JSON-WEB-TOKEN"] = legacyToken
            view!!.loadUrl(url, additionalHttpHeaders)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}