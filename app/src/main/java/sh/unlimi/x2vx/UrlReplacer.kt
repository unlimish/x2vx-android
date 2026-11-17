package sh.unlimi.x2vx

import java.net.URI

class UrlReplacer(
    /** Proxy used for x.com links. */
    private val provider: Provider = Provider.DEFAULT,
    /** Language code to translate posts into. Ignored when the provider doesn't support translation. */
    private val translateTo: String? = null,
) {
    companion object {
        private val STATUS_PATH = Regex("^/[^/]+/status/\\d+/?$")
        private val LANGUAGE_CODE = Regex("^[a-zA-Z]{2,3}(-[a-zA-Z0-9]{2,8})?$")
        private val URL_IN_TEXT = Regex("https?://\\S+")

        /**
         * Fixed proxies for platforms other than X, keyed by the original host.
         * TikTok short links (vm./vt.) keep their subdomain.
         */
        private val OTHER_PLATFORMS = mapOf(
            "tiktok.com" to "tnktok.com",
            "www.tiktok.com" to "tnktok.com",
            "m.tiktok.com" to "tnktok.com",
            "vm.tiktok.com" to "vm.tnktok.com",
            "vt.tiktok.com" to "vt.tnktok.com",
            "bsky.app" to "fxbsky.app",
            "reddit.com" to "vxreddit.com",
            "www.reddit.com" to "vxreddit.com",
            "old.reddit.com" to "vxreddit.com",
            "new.reddit.com" to "vxreddit.com",
            "np.reddit.com" to "vxreddit.com",
        )

        /**
         * Extracts the first http(s) URL from shared text,
         * e.g. "Check this out! https://vm.tiktok.com/abc/".
         */
        fun findUrl(text: String): URI? {
            val match = URL_IN_TEXT.find(text) ?: return null
            return try {
                URI(match.value)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun supports(url: URI): Boolean {
        val host = url.host?.lowercase() ?: return false
        return host == "x.com" || host in OTHER_PLATFORMS
    }

    fun replaceDomainName(url: URI): URI {
        if (!supports(url)) {
            return url
        }

        val host = url.host.lowercase()
        val isX = host == "x.com"

        return URI(
            url.scheme,
            url.userInfo,
            if (isX) provider.host else OTHER_PLATFORMS.getValue(host),
            url.port,
            if (isX) buildXPath(url.path) else url.path,
            // removes query and fragment
            null,
            null,
        )
    }

    private fun buildXPath(path: String?): String? {
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
