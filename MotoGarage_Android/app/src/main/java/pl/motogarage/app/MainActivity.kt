
package pl.motogarage.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.webkit.*
import android.net.Uri
import android.widget.FrameLayout
import android.view.ViewGroup
import android.view.View
import android.view.WindowInsets
import android.os.Build
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var chooser: ValueCallback<Array<Uri>>? = null
    private val pickCode = 107

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Draw behind both system bars and apply their insets exactly once.
        // This keeps the MotoGarage header below the clock, while the bottom
        // tabs touch the Android navigation area without a second empty strip.
        val dark = Color.rgb(16, 19, 24)
        window.statusBarColor = dark
        window.navigationBarColor = dark

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.decorView.systemUiVisibility = 0
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        web = WebView(this)
        web.setBackgroundColor(dark)

        val container = FrameLayout(this)
        container.setBackgroundColor(dark)
        container.addView(web, FrameLayout.LayoutParams(-1, -1))

        container.setOnApplyWindowInsetsListener { view: View, insets: WindowInsets ->
            val top: Int
            val bottom: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bars = insets.getInsets(
                    WindowInsets.Type.statusBars() or
                    WindowInsets.Type.navigationBars()
                )
                top = bars.top
                bottom = bars.bottom
            } else {
                @Suppress("DEPRECATION")
                top = insets.systemWindowInsetTop
                @Suppress("DEPRECATION")
                bottom = insets.systemWindowInsetBottom
            }

            // Header and fixed HTML navigation remain inside this padded area.
            view.setPadding(0, top, 0, bottom)
            insets
        }

        setContentView(container, ViewGroup.LayoutParams(-1, -1))
        container.requestApplyInsets()

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url
                if (url.scheme == "https" || url.scheme == "geo") {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, url))
                    } catch (_: Exception) {}
                    return true
                }
                return false
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {
                chooser?.onReceiveValue(null)
                chooser = callback

                return try {
                    startActivityForResult(
                        params?.createIntent()
                            ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "*/*"
                                addCategory(Intent.CATEGORY_OPENABLE)
                            },
                        pickCode
                    )
                    true
                } catch (_: Exception) {
                    chooser = null
                    false
                }
            }
        }

        web.loadUrl("file:///android_asset/index.html")
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickCode) {
            chooser?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            )
            chooser = null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack()
        else super.onBackPressed()
    }
}
