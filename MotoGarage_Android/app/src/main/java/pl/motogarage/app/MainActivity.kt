package pl.motogarage.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.webkit.*
import android.net.Uri
import android.view.ViewGroup

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var chooser: ValueCallback<Array<Uri>>? = null
    private val pickCode = 107
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setContentView(web, ViewGroup.LayoutParams(-1,-1))
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                if (url.scheme == "https" || url.scheme == "geo") {
                    try { startActivity(Intent(Intent.ACTION_VIEW, url)) } catch (_: Exception) {}
                    return true
                }
                return false
            }
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(view: WebView?, callback: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                chooser?.onReceiveValue(null)
                chooser = callback
                return try { startActivityForResult(params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type="*/*"; addCategory(Intent.CATEGORY_OPENABLE) }, pickCode); true }
                catch (_: Exception) { chooser=null; false }
            }
        }
        web.loadUrl("file:///android_asset/index.html")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == pickCode) { chooser?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data)); chooser=null }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { if(web.canGoBack()) web.goBack() else super.onBackPressed() }
}
