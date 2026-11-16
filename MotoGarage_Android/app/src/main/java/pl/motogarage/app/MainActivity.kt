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
import java.io.File
import java.security.MessageDigest

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var chooser: ValueCallback<Array<Uri>>? = null
    private val pickCode = 107
    private val saveBackupCode = 108
    private val importBackupCode = 109
    private var pendingBackupFile: File? = null
    private var preparingBackup = false
    private var importOpen = false
    private val languagePrefs by lazy { getSharedPreferences("motogarage_language", MODE_PRIVATE) }
    @Volatile private var appLanguage = "en"

    private fun deviceLanguageTag(): String = resources.configuration.locales[0].language
    private fun localized(pl: String, en: String): String = if (appLanguage == "pl") pl else en


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appLanguage = languagePrefs.getString("language", null)
            ?: if (deviceLanguageTag().startsWith("pl")) "pl" else "en"
        // Only a small filename crosses the Android saved-state boundary, never JSON/photos.
        savedInstanceState?.getString("pendingBackupFile")?.let { name ->
            if (name.matches(Regex("backup-[A-Za-z0-9-]+\\.json"))) {
                File(File(filesDir, "pending_exports"), name).takeIf { it.isFile }?.let {
                    pendingBackupFile = it
                }
            }
        }
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
                    catch (_: Exception) { notifyUser(localized("Nie można otworzyć linku.", "Could not open the link.")) }
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
        fun deviceLanguage(): String = deviceLanguageTag()
        @JavascriptInterface
        fun setLanguage(language: String) {
            if (language != "pl" && language != "en") return
            appLanguage = language
            languagePrefs.edit().putString("language", language).apply()
        }

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
        if (preparingBackup || pendingBackupFile != null) {
            notifyUser(localized("Zakończ poprzedni zapis kopii.", "Finish saving the previous backup first."))
            return
        }
        preparingBackup = true
        val safeName = name.substringAfterLast('/').substringAfterLast('\\')
            .ifBlank { "MotoGarage_kopia.json" }
        Thread {
            var staged: File? = null
            try {
                if (json.isBlank()) throw IllegalStateException(localized("Kopia jest pusta.", "The backup is empty."))
                val dir = File(filesDir, "pending_exports")
                if (!dir.isDirectory && !dir.mkdirs()) throw IllegalStateException("Cannot create export directory")
                val file = File.createTempFile("backup-", ".json", dir)
                staged = file
                file.outputStream().use { output ->
                    output.writer(Charsets.UTF_8).apply { write(json); flush() }
                    output.fd.sync()
                }
                if (file.length() <= 0L || file.length() > 32L * 1024 * 1024) {
                    throw IllegalStateException(localized("Kopia jest pusta lub przekracza limit 32 MB.", "The backup is empty or exceeds the 32 MB limit."))
                }
                runOnUiThread {
                    preparingBackup = false
                    if (isFinishing || isDestroyed) {
                        file.delete()
                    } else {
                        pendingBackupFile = file
                        try {
                            startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "application/json"
                                putExtra(Intent.EXTRA_TITLE, safeName)
                            }, saveBackupCode)
                        } catch (_: Exception) {
                            pendingBackupFile = null
                            file.delete()
                            notifyUser(localized("Nie można otworzyć okna zapisu kopii.", "Could not open the backup save dialog."))
                        }
                    }
                }
            } catch (e: Exception) {
                staged?.delete()
                runOnUiThread {
                    preparingBackup = false
                    notifyUser(localized("Nie udało się przygotować kopii: ${e.message}", "Could not prepare the backup: ${e.message}"))
                }
            }
        }.start()
    }

    // Stream the verification as well, so photos are not loaded a second time into memory.
    private fun digest(input: java.io.InputStream): ByteArray {
        val hash = MessageDigest.getInstance("SHA-256")
        input.use { stream ->
            val buffer = ByteArray(8192)
            while (true) {
                val count = stream.read(buffer)
                if (count < 0) break
                if (count > 0) hash.update(buffer, 0, count)
            }
        }
        return hash.digest()
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
            notifyUser(localized("Nie można otworzyć wyboru kopii.", "Could not open the backup picker."))
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        pendingBackupFile?.let { outState.putString("pendingBackupFile", it.name) }
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
            val source = pendingBackupFile
            pendingBackupFile = null
            if (resultCode != RESULT_OK) {
                source?.delete()
                notifyUser(localized("Anulowano zapis kopii.", "Backup save canceled."))
                return
            }
            val uri = data?.data
            if (uri == null || source == null || !source.isFile || source.length() <= 0L) {
                source?.delete()
                notifyUser(localized("Nie udało się zapisać kopii. Spróbuj ponownie.", "Could not save the backup. Try again."))
                return
            }
            Thread {
                try {
                    val expectedSize = source.length()
                    val output = contentResolver.openOutputStream(uri, "wt")
                        ?: throw IllegalStateException(localized("Brak dostępu do pliku", "Cannot access the file"))
                    val written = output.use { destination ->
                        source.inputStream().use { it.copyTo(destination) }
                    }
                    if (written != expectedSize) throw IllegalStateException("Incomplete backup write")
                    val saved = contentResolver.openInputStream(uri)
                        ?: throw IllegalStateException("Cannot verify saved backup")
                    if (!digest(source.inputStream()).contentEquals(digest(saved))) {
                        throw IllegalStateException(localized("Zapisany plik różni się od kopii.", "The saved file does not match the backup."))
                    }
                    runOnUiThread { notifyUser(localized("Kopia zapasowa została zapisana.", "Backup saved.")) }
                } catch (e: Exception) {
                    runOnUiThread { notifyUser(localized("Nie udało się zapisać i zweryfikować kopii. Spróbuj ponownie. ${e.message}", "Could not save and verify the backup. Try again. ${e.message}")) }
                } finally {
                    source.delete()
                }
            }.start()
            return
        }
        if (requestCode == importBackupCode) {
            importOpen = false
            if (resultCode != RESULT_OK) return
            val uri = data?.data
            if (uri == null) {
                notifyUser(localized("Nie otrzymano wybranego pliku.", "The selected file was not received."))
                return
            }
            Thread {
                try {
                    // Czytamy strumień, niezależnie od rozmiaru podanego przez dostawcę pliku.
                    val input = contentResolver.openInputStream(uri)
                        ?: throw IllegalStateException(localized("Brak dostępu do pliku", "Cannot access the file"))
                    val bytes = input.use { stream ->
                        val output = ByteArrayOutputStream()
                        val buffer = ByteArray(8192)
                        while (true) {
                            val count = stream.read(buffer)
                            if (count < 0) break
                            if (output.size() + count > 32 * 1024 * 1024) {
                                throw IllegalStateException(localized("Kopia przekracza limit 32 MB", "The backup exceeds the 32 MB limit"))
                            }
                            output.write(buffer, 0, count)
                        }
                        output.toByteArray()
                    }
                    val text = String(bytes, Charsets.UTF_8).removePrefix("\uFEFF").trim()
                    if (text.isEmpty()) throw IllegalStateException(localized("Android odczytał pusty plik", "Android read an empty file"))
                    JSONObject(text)
                    val quoted = JSONObject.quote(text)
                        .replace("\u2028", "\\u2028").replace("\u2029", "\\u2029")
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            web.evaluateJavascript("window.receiveNativeBackup($quoted);", null)
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { notifyUser(localized("Nie udało się odczytać kopii: ${e.message}", "Could not read the backup: ${e.message}")) }
                }
            }.start()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
