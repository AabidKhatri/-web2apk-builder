
package com.example.web2apk

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var swipeRefresh: SwipeRefreshLayout? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.SCREEN_MODE == "fullscreen" ||
            BuildConfig.SCREEN_MODE == "kiosk"
        ) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        val root = FrameLayout(this)

        val content = if (BuildConfig.PULL_REFRESH) {
            SwipeRefreshLayout(this).also {
                swipeRefresh = it
            }
        } else {
            null
        }

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.loadsImagesAutomatically = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url ?: return false
                    val scheme = uri.scheme?.lowercase()

                    if (scheme == "http" || scheme == "https") {
                        val host = uri.host.orEmpty()
                        val initialHost =
                            Uri.parse(BuildConfig.WEB_URL).host.orEmpty()

                        if (
                            BuildConfig.EXTERNAL_LINKS == "browser" &&
                            host != initialHost
                        ) {
                            return openExternal(uri)
                        }

                        return false
                    }

                    if (scheme == "intent") {
                        return openExternal(uri)
                    }

                    return true
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    swipeRefresh?.isRefreshing = false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        swipeRefresh?.isRefreshing = false
                        Toast.makeText(
                            this@MainActivity,
                            "Page failed to load. Check your connection.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        if (content != null) {
            content.setColorSchemeColors(
                android.graphics.Color.rgb(196, 241, 45)
            )

            content.setOnRefreshListener {
                webView.reload()
            }

            content.addView(
                webView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            root.addView(
                content,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        } else {
            root.addView(
                webView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        setContentView(root)

        webView.loadUrl(BuildConfig.WEB_URL)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else if (BuildConfig.EXIT_CONFIRMATION) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Exit app?")
                            .setMessage("Do you want to close this app?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Exit") { _, _ ->
                                finish()
                            }
                            .show()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    private fun openExternal(uri: Uri): Boolean {
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
