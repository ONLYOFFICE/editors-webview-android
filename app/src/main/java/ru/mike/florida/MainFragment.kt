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
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class MainFragment : Fragment(R.layout.fragment_main) {

    private var webView: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.main_web_view)
        setSettings()
        if (BuildConfig.DOCUMENT_SERVER_URL.isEmpty()) {
            showDialog()
        } else {
            webView?.loadUrl(BuildConfig.DOCUMENT_SERVER_URL)
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
        webView?.webViewClient = MainWebViewClient(findNavController())
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Document server url is empty.\nYou must specify the address in build.gradle")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()
            }
            .create()
            .show()
    }
}

private class MainWebViewClient(private val navController: NavController) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url
        if (url != null) {
            val path = url.path
            if (path?.contains("editor") == true) {
                navController.navigate(R.id.action_mainFragment_to_editorFragment, Bundle(1).apply {
                    putString("document_url", url.toString())
                })
                return true
            }
            return false
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

}