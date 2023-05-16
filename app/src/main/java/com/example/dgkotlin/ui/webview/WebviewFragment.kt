package com.example.dgkotlin.ui.webview

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.dgkotlin.BuildConfig
import com.example.dgkotlin.R

class WebviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val webview: WebView = view.findViewById(R.id.frag_webview)

        webview.setWebViewClient(WebViewClientImpl(requireActivity()))
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true

        val prefs: SharedPreferences = requireActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val legacyToken: String? = prefs.getString(BuildConfig.JWT_LEGACY_TOKEN_PREF_KEY, null)

        webview.addJavascriptInterface(WebAppInterface(context), "NativeWebInterface")

        val additionalHttpHeaders = mutableMapOf<String, String?>()
        additionalHttpHeaders["X-JSON-WEB-TOKEN"] = legacyToken

        val quoteId: Long? = arguments?.getLong("quoteId")
        if (quoteId != null) {
            webview.loadUrl(BuildConfig.WEB_VIEW_QUOTE_URL.replace("{quoteId}", quoteId.toString()), additionalHttpHeaders)
        } else {
            webview.loadUrl(BuildConfig.WEB_VIEW_URL, additionalHttpHeaders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}