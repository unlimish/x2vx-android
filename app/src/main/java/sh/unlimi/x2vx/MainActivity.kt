// MainActivity.kt
package sh.unlimi.x2vx

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URI

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called")
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent called with action: ${intent.action}")
        when (intent.action) {
            Intent.ACTION_SEND -> {
                val urlString = intent.getStringExtra(Intent.EXTRA_TEXT)
                Log.d(TAG, "Received ACTION_SEND with text: $urlString")
                urlString
                    ?.let { URI(it) }
                    ?.let {
                        val replacer = UrlReplacer()
                        if (replacer.supports(it)) {
                            val modifiedUrl = replacer.replaceDomainName(it).toString()
                            copyToClipboard(modifiedUrl)
                            Toast.makeText(this, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Modified URL", text)
        clipboard.setPrimaryClip(clip)
    }
}