package ru.mike.florida

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class EditorFragment : Fragment(R.layout.fragment_editor) {

    private var url: Uri? = null

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = Uri.parse(arguments?.getString("document_url"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.editor_web_view)
        setSettings()
        load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView?.apply {
            clearCache(true)
            clearHistory()
            clearFormData()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSettings() {
        webView?.settings?.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
        }
        webView?.webViewClient = EditorWebViewClient(findNavController())
    }

    private fun load() {
        url?.let {
            val loadUrl = it.toString()
            if (it.query?.contains("docxf") == true) {
                webView?.loadUrl(loadUrl)
            } else {
                webView?.loadUrl(loadUrl.replace("type=desktop", "type=mobile"))
            }
        }
    }

}

private class EditorWebViewClient(private val navController: NavController) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let { url ->
            if (!url.toString().contains("editor")) {
                navController.popBackStack()
                return true
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

}
