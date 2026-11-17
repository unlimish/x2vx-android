package sh.unlimi.x2vx

import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {
    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_PROVIDER = "provider"
        private const val KEY_TRANSLATE_ENABLED = "translate_enabled"
        private const val KEY_TRANSLATE_LANGUAGE = "translate_language"
        const val DEFAULT_LANGUAGE = "en"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var provider: Provider
        get() = Provider.fromName(prefs.getString(KEY_PROVIDER, null))
        set(value) = prefs.edit().putString(KEY_PROVIDER, value.name).apply()

    var translateEnabled: Boolean
        get() = prefs.getBoolean(KEY_TRANSLATE_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_TRANSLATE_ENABLED, value).apply()

    var translateLanguage: String
        get() = prefs.getString(KEY_TRANSLATE_LANGUAGE, null) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_TRANSLATE_LANGUAGE, value).apply()

    fun createReplacer(): UrlReplacer = UrlReplacer(
        provider = provider,
        translateTo = if (translateEnabled) translateLanguage else null,
    )
}
