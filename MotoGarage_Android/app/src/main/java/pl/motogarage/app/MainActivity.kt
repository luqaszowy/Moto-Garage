package pl.motogarage.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var chooser: ValueCallback<Array<Uri>>? = null
    private val pickCode = 107
    private val saveBackupCode = 108
    private val importBackupCode = 109
    private var pendingBackup: String? = null
    private var importOpen = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingBackup = savedInstanceState?.getString("pendingBackup")
        val dark = Color.rgb(16, 19, 24)
        window.statusBarColor = dark
        window.navigationBarColor = dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bars = insets.getInsets(
                    WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
                )
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            } else {
                view.setPadding(
                    insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom
                )
            }
            insets
        }
        setContentView(container, ViewGroup.LayoutParams(-1, -1))
        container.requestApplyInsets()
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true
        web.addJavascriptInterface(BackupBridge(), "MotoGarageBackup")
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                if (url.toString() == "file:///android_asset/index.html") return false
                if (url.scheme == "https" || url.scheme == "geo") {
                    try { startActivity(Intent(Intent.ACTION_VIEW, url)) }
                    catch (_: Exception) { notifyUser("Nie można otworzyć linku.") }
                }
                return true
            }
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?, callback: ValueCallback<Array<Uri>>?, params: FileChooserParams?
            ): Boolean {
                chooser?.onReceiveValue(null)
                chooser = callback
                return try {
                    startActivityForResult(
                        params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }, pickCode
                    )
                    true
                } catch (_: Exception) {
                    chooser?.onReceiveValue(null)
                    chooser = null
                    true
                }
            }
        }
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class BackupBridge {
        @JavascriptInterface
        fun saveBackup(name: String, json: String) {
            runOnUiThread { openBackupDestination(name, json) }
        }
        @JavascriptInterface
        fun importBackup() {
            runOnUiThread { openBackupSource() }
        }
    }

    @Suppress("DEPRECATION")
    private fun openBackupDestination(name: String, json: String) {
        if (pendingBackup != null) {
            notifyUser("Zakończ poprzedni zapis kopii.")
            return
        }
        pendingBackup = json
        val safeName = name.substringAfterLast('/').substringAfterLast('\\')
            .ifBlank { "MotoGarage_kopia.json" }
        try {
            startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, safeName)
            }, saveBackupCode)
        } catch (_: Exception) {
            pendingBackup = null
            notifyUser("Nie można otworzyć okna zapisu kopii.")
        }
    }

    @Suppress("DEPRECATION")
    private fun openBackupSource() {
        if (importOpen) return
        importOpen = true
        try {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                // Niektóre menedżery oznaczają JSON jako zwykły tekst.
                type = "*/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, importBackupCode)
        } catch (_: Exception) {
            importOpen = false
            notifyUser("Nie można otworzyć wyboru kopii.")
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        pendingBackup?.let { outState.putString("pendingBackup", it) }
        super.onSaveInstanceState(outState)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickCode) {
            chooser?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            chooser = null
            return
        }
        if (requestCode == saveBackupCode) {
            val json = pendingBackup
            pendingBackup = null
            if (resultCode != RESULT_OK) {
                notifyUser("Anulowano zapis kopii.")
                return
            }
            val uri = data?.data
            if (uri == null || json == null) {
                notifyUser("Nie udało się zapisać kopii. Spróbuj ponownie.")
                return
            }
            Thread {
                try {
                    val output = contentResolver.openOutputStream(uri, "wt")
                        ?: throw IllegalStateException("Brak dostępu do pliku")
                    output.bufferedWriter(Charsets.UTF_8).use { it.write(json) }
                    runOnUiThread { notifyUser("Kopia zapasowa została zapisana.") }
                } catch (_: Exception) {
                    runOnUiThread { notifyUser("Nie udało się zapisać kopii.") }
                }
            }.start()
            return
        }
        if (requestCode == importBackupCode) {
            importOpen = false
            if (resultCode != RESULT_OK) return
            val uri = data?.data
            if (uri == null) {
                notifyUser("Nie otrzymano wybranego pliku.")
                return
            }
            Thread {
                try {
                    // Czytamy strumień, niezależnie od rozmiaru podanego przez dostawcę pliku.
                    val input = contentResolver.openInputStream(uri)
                        ?: throw IllegalStateException("Brak dostępu do pliku")
                    val bytes = input.use { stream ->
                        val output = ByteArrayOutputStream()
                        val buffer = ByteArray(8192)
                        while (true) {
                            val count = stream.read(buffer)
                            if (count < 0) break
                            if (output.size() + count > 32 * 1024 * 1024) {
                                throw IllegalStateException("Kopia przekracza limit 32 MB")
                            }
                            output.write(buffer, 0, count)
                        }
                        output.toByteArray()
                    }
                    val text = String(bytes, Charsets.UTF_8).removePrefix("\uFEFF").trim()
                    if (text.isEmpty()) throw IllegalStateException("Android odczytał pusty plik")
                    JSONObject(text)
                    val quoted = JSONObject.quote(text)
                        .replace("\u2028", "\\u2028").replace("\u2029", "\\u2029")
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            web.evaluateJavascript("window.receiveNativeBackup($quoted);", null)
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { notifyUser("Nie udało się odczytać kopii: ${e.message}") }
                }
            }.start()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
