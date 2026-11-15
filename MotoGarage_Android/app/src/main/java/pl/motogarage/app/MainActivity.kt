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
