// MainActivity.kt
package sh.unlimi.x2vx

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.net.URI
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        private const val SAMPLE_URL = "https://x.com/unlimish/status/1773686985728962615"

        /** Languages offered for translation (ISO 639-1 codes accepted by FxEmbed). */
        private val LANGUAGES = listOf(
            "en", "ja", "ko", "zh", "es", "fr", "de", "it", "pt", "ru",
            "ar", "hi", "id", "th", "vi", "tr", "nl", "pl", "uk", "sv",
        )
    }

    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = Settings(this)
        Log.d(TAG, "onCreate called")
        if (intent.action == Intent.ACTION_SEND) {
            handleShare(intent)
        } else {
            showSettings()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called")
        if (intent.action == Intent.ACTION_SEND) {
            handleShare(intent)
        }
    }

    private fun handleShare(intent: Intent) {
        val urlString = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim()
        Log.d(TAG, "Received ACTION_SEND with text: $urlString")
        val uri = urlString?.let { UrlReplacer.findUrl(it) }
        val replacer = settings.createReplacer()
        if (uri != null && replacer.supports(uri)) {
            val converted = replacer.replaceDomainName(uri).toString()
            copyToClipboard(converted)
            Toast.makeText(this, R.string.toast_copied, Toast.LENGTH_SHORT).show()
            if (settings.shareAfterConvert) {
                shareText(converted)
            }
        } else {
            Toast.makeText(this, R.string.toast_invalid_url, Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun showSettings() {
        setContentView(R.layout.activity_main)

        val providerGroup = findViewById<RadioGroup>(R.id.provider_group)
        val translateSwitch = findViewById<SwitchCompat>(R.id.translate_switch)
        val languageSpinner = findViewById<Spinner>(R.id.language_spinner)
        val translateNote = findViewById<TextView>(R.id.translate_note)
        val preview = findViewById<TextView>(R.id.preview)
        val shareSwitch = findViewById<SwitchCompat>(R.id.share_switch)

        // Provider radio buttons
        val buttonIds = mutableMapOf<Int, Provider>()
        Provider.entries.forEach { provider ->
            val button = RadioButton(this).apply {
                id = View.generateViewId()
                text = provider.host
                textSize = 16f
            }
            buttonIds[button.id] = provider
            providerGroup.addView(button)
            if (provider == settings.provider) button.isChecked = true
        }

        // Language spinner
        val labels = LANGUAGES.map { code ->
            val name = Locale.forLanguageTag(code).getDisplayLanguage(Locale.forLanguageTag(code))
            "${name.replaceFirstChar { it.titlecase(Locale.getDefault()) }} ($code)"
        }
        languageSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        val savedLanguage = settings.translateLanguage
        languageSpinner.setSelection(LANGUAGES.indexOf(savedLanguage).coerceAtLeast(0))
        translateSwitch.isChecked = settings.translateEnabled
        shareSwitch.isChecked = settings.shareAfterConvert
        shareSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.shareAfterConvert = isChecked
        }

        fun refresh() {
            val supports = settings.provider.supportsTranslation
            translateSwitch.isEnabled = supports
            languageSpinner.isEnabled = supports && settings.translateEnabled
            translateNote.visibility = if (supports) View.GONE else View.VISIBLE
            preview.text = settings.createReplacer().replaceDomainName(URI(SAMPLE_URL)).toString()
        }

        providerGroup.setOnCheckedChangeListener { _, checkedId ->
            buttonIds[checkedId]?.let { settings.provider = it }
            refresh()
        }
        translateSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.translateEnabled = isChecked
            refresh()
        }
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settings.translateLanguage = LANGUAGES[position]
                refresh()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        refresh()
    }

    private fun shareText(text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(send, null).apply {
            // Don't offer x2vx itself as a target.
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(ComponentName(this@MainActivity, MainActivity::class.java)))
        }
        startActivity(chooser)
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Modified URL", text)
        clipboard.setPrimaryClip(clip)
    }
}
