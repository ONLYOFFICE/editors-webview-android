/*
 * Copyright (C) Ascensio System SIA, 2009-2026
 *
 * This program is a free software product. You can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License (AGPL)
 * version 3 as published by the Free Software Foundation, together with the
 * additional terms provided in the LICENSE file.
 *
 * This program is distributed WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. For
 * details, see the GNU AGPL at: https://www.gnu.org/licenses/agpl-3.0.html
 *
 * You can contact Ascensio System SIA by email at info@onlyoffice.com
 * or by postal mail at 20A-6 Ernesta Birznieka-Upisha Street, Riga,
 * LV-1050, Latvia, European Union.
 *
 * The interactive user interfaces in modified versions of the Program
 * are required to display Appropriate Legal Notices in accordance with
 * Section 5 of the GNU AGPL version 3.
 *
 * No trademark rights are granted under this License.
 *
 * All non-code elements of the Product, including illustrations,
 * icon sets, and technical writing content, are licensed under the
 * Creative Commons Attribution-ShareAlike 4.0 International License:
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * This license applies only to such non-code elements and does not
 * modify or replace the licensing terms applicable to the Program's
 * source code, which remains licensed under the GNU Affero General
 * Public License v3.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

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
