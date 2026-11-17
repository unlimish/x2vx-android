package sh.unlimi.x2vx

import java.net.URI

class UrlReplacer(
    private val provider: Provider = Provider.DEFAULT,
    /** Language code to translate posts into. Ignored when the provider doesn't support translation. */
    private val translateTo: String? = null,
) {
    companion object {
        private val STATUS_PATH = Regex("^/[^/]+/status/\\d+/?$")
        private val LANGUAGE_CODE = Regex("^[a-zA-Z]{2,3}(-[a-zA-Z0-9]{2,8})?$")
    }

    fun supports(url: URI): Boolean {
        return url.host == "x.com"
    }

    fun replaceDomainName(url: URI): URI {
        if (!supports(url)) {
            return url
        }

        return URI(
            url.scheme,
            url.userInfo,
            provider.host,
            url.port,
            buildPath(url.path),
            // removes query and fragment
            null,
            null,
        )
    }

    private fun buildPath(path: String?): String? {
        val lang = translateTo?.trim()
        if (path == null ||
            !provider.supportsTranslation ||
            lang.isNullOrEmpty() ||
            !LANGUAGE_CODE.matches(lang) ||
            !STATUS_PATH.matches(path)
        ) {
            return path
        }
        return path.trimEnd('/') + "/" + lang.lowercase()
    }
}
